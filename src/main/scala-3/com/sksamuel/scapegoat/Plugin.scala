package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.io.IOUtils
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.{ctx, Context}
import dotty.tools.dotc.plugins.{PluginPhase, StandardPlugin}
import dotty.tools.dotc.reporting.Diagnostic
import dotty.tools.dotc.reporting.Diagnostic.{Error, Info, Warning}
import dotty.tools.dotc.transform.PatternMatcher
import dotty.tools.dotc.util.NoSourcePosition

import java.io.File

class ScapegoatPlugin extends StandardPlugin {

  override def name: String = "scapegoat"

  override def description: String = "scapegoat compiler plugin"

  override val optionsHelp: Option[String] = Some(Configuration.optionsHelp)

  override def init(options: List[String]): List[PluginPhase] = {
    val config = Configuration.fromPluginOptions(options)
    new ScapegoatPhase(config, Inspections.inspections) :: Nil
  }

}

class ScapegoatPhase(var configuration: Configuration, override val inspections: Seq[Inspection])
    extends PluginPhase
    with ScapegoatBasePlugin {

  import tpd.*

  private var feedback: Option[FeedbackDotty] = None

  override def phaseName: String = "scapegoat"

  // TODO(johan): Where is the proper type exposing this name?
  override val runsAfter: Set[String] = Set("typer")

  override val runsBefore: Set[String] = Set(PatternMatcher.name)

  override def run(using Context): Unit = {

    import dotty.tools.dotc.core.Decorators.toMessage

    if (disableAll) {
      ctx.reporter.report(Info("[scapegoat] All inspections disabled", NoSourcePosition))
    } else {
      val feedbackDotty = new FeedbackDotty(configuration)
      feedback = Some(feedbackDotty)
      super.run

      val errors = feedbackDotty.errors.size
      val warns = feedbackDotty.warns.size
      val infos = feedbackDotty.infos.size
      val level: String => Diagnostic =
        if (errors > 0)
          msg => Diagnostic.Error(msg, NoSourcePosition)
        else if (warns > 0)
          msg => Diagnostic.Warning(msg.toMessage, NoSourcePosition)
        else
          msg => Diagnostic.Info(msg, NoSourcePosition)
      ctx.reporter.report(level(s"[scapegoat] Analysis complete: $errors errors $warns warns $infos infos"))

      val reports = configuration.reports
      writeReport(reports.disableHTML, "HTML", feedbackDotty, IOUtils.writeHTMLReport)
      writeReport(reports.disableXML, "XML", feedbackDotty, IOUtils.writeXMLReport)
      writeReport(
        reports.disableScalastyleXML,
        "Scalastyle XML",
        feedbackDotty,
        IOUtils.writeScalastyleReport
      )
      writeReport(reports.disableMarkdown, "Markdown", feedbackDotty, IOUtils.writeMarkdownReport)
      writeReport(
        reports.disableGitlabCodeQuality,
        "GitLab Code Quality",
        feedbackDotty,
        IOUtils.writeGitlabCodeQualityReport
      )
    }
  }

  override def transformUnit(tree: tpd.Tree)(using ctx: Context): tpd.Tree = {
    val inspections = activeInspections
    if (configuration.verbose) {
      ctx.reporter.report(Info(s"Running with ${inspections.size} active inspections", NoSourcePosition))
    }
    inspections.foreach { inspection =>
      feedback.foreach(f => inspection.inspect(f, tree))
    }
    tree
  }

  private def writeReport(
    reportDisabled: Boolean,
    reportName: String,
    feedback: FeedbackDotty,
    writer: (File, Feedback[?]) => File
  )(using ctx: Context): Unit = {
    if (!reportDisabled) {
      configuration.dataDir.foreach { outputDir =>
        val output = writer(outputDir, feedback)
        if (configuration.verbose)
          ctx.reporter.report(Info(s"[scapegoat] Written $reportName report [$output]", NoSourcePosition))
      }
    }
  }

}
