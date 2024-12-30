package com.sksamuel.scapegoat

import java.io.File

import com.sksamuel.scapegoat.io.IOUtils
import dotty.tools.dotc.CompilationUnit
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.plugins.{PluginPhase, StandardPlugin}
import dotty.tools.dotc.reporting.Diagnostic
import dotty.tools.dotc.reporting.Diagnostic.{Error, Info, Warning}
import dotty.tools.dotc.transform.PatternMatcher
import dotty.tools.dotc.typer.TyperPhase
import dotty.tools.dotc.util.NoSourcePosition

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

  private[scapegoat] var feedback: Option[FeedbackDotty] = None

  override def phaseName: String = "scapegoat"

  override val runsAfter: Set[String] = Set(TyperPhase.name)

  override val runsBefore: Set[String] = Set(PatternMatcher.name)

  override def runOn(units: List[CompilationUnit])(using runCtx: Context): List[CompilationUnit] = {
    import dotty.tools.dotc.core.Decorators.toMessage

    if (disableAll) {
      runCtx.reporter.report(Info("[scapegoat] All inspections disabled", NoSourcePosition))
      units
    } else {
      if (configuration.verbose) {
        runCtx.reporter.report(
          Info(s"[scapegoat] Running with ${activeInspections.size} active inspections", NoSourcePosition)
        )
      }

      val feedbackDotty = new FeedbackDotty(configuration)
      feedback = Some(feedbackDotty)
      val ran = super.runOn(units)

      val errors = feedbackDotty.errors.size
      val warns = feedbackDotty.warns.size
      val infos = feedbackDotty.infos.size
      val msg = s"[scapegoat] Analysis complete: $errors errors $warns warns $infos infos"
      val level: Diagnostic =
        if (errors > 0)
          Diagnostic.Error(msg, NoSourcePosition)
        else if (warns > 0)
          Diagnostic.Warning(msg.toMessage, NoSourcePosition)
        else
          Diagnostic.Info(msg, NoSourcePosition)
      runCtx.reporter.report(level)

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

      ran
    }
  }

  override def transformUnit(tree: tpd.Tree)(using ctx: Context): tpd.Tree = {
    activeInspections.foreach { inspection =>
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
