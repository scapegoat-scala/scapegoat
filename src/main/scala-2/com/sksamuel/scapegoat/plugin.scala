package com.sksamuel.scapegoat

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

import scala.tools.nsc._
import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.transform.{Transform, TypingTransformers}

import com.sksamuel.scapegoat.io.IOUtils

class ScapegoatPlugin(val global: Global) extends Plugin {

  override val name: String = "scapegoat"
  override val description: String = "scapegoat compiler plugin"
  val component = new ScapegoatComponent(global, Inspections.inspections)
  override val components: List[PluginComponent] = List(component)

  override def init(options: List[String], error: String => Unit): Boolean = {
    component.configuration = Configuration.fromPluginOptions(options)
    if (component.configuration.dataDir.isEmpty) {
      error("-P:scapegoat:dataDir not specified")
    }
    component.configuration.dataDir.isDefined
  }

  override val optionsHelp: Option[String] = Some(Configuration.optionsHelp)
}

class ecapegoatComponent(val global: Global, override val inspections: Seq[Inspection])
    extends PluginComponent
    with ScapegoatBasePlugin
    with TypingTransformers
    with Transform {

  require(inspections != null)

  import global._

  override var configuration: Configuration = null

  val debug: Boolean = false
  var summary: Boolean = true

  private val count = new AtomicInteger(0)

  override val phaseName: String = "scapegoat"
  override val runsAfter: List[String] = List("typer")
  override val runsBefore = List[String]("patmat")

  lazy val feedback = new Feedback(global.reporter, configuration)

  def writeReport(isDisabled: Boolean, reportName: String, writer: (File, Feedback) => File): Unit = {
    if (!isDisabled) {
      configuration.dataDir.foreach { outputDir =>
        val output = writer(outputDir, feedback)
        if (configuration.verbose)
          reporter.echo(s"[info] [scapegoat] Written $reportName report [$output]")
      }
    }
  }

  override def newPhase(prev: scala.tools.nsc.Phase): Phase =
    new Phase(prev) {
      override def run(): Unit = {
        if (disableAll)
          reporter.echo("[info] [scapegoat] All inspections disabled")
        else {
          reporter.echo(s"[info] [scapegoat] ${activeInspections.size} activated inspections")
          if (configuration.verbose)
            if (configuration.ignoredFiles.nonEmpty)
              reporter.echo(s"[info] [scapegoat] ${configuration.ignoredFiles} ignored file patterns")
          super.run()

          if (summary) {
            val errors = feedback.errors.size
            val warns = feedback.warns.size
            val infos = feedback.infos.size
            val level = if (errors > 0) "error" else if (warns > 0) "warn" else "info"
            reporter.echo(
              s"[$level] [scapegoat] Analysis complete: ${count.get} files - $errors errors $warns warns $infos infos"
            )
          }

          val reports = configuration.reports
          writeReport(reports.disableHTML, "HTML", IOUtils.writeHTMLReport)
          writeReport(reports.disableXML, "XML", IOUtils.writeXMLReport)
          writeReport(reports.disableScalastyleXML, "Scalastyle XML", IOUtils.writeScalastyleReport)
          writeReport(reports.disableMarkdown, "Markdown", IOUtils.writeMarkdownReport)
          writeReport(
            reports.disableGitlabCodeQuality,
            "GitLab Code Quality",
            IOUtils.writeGitlabCodeQualityReport
          )
        }
      }
    }

  protected def newTransformer(unit: CompilationUnit): Transformer = {
    count.incrementAndGet()
    new Transformer(unit)
  }

  class Transformer(unit: global.CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: global.Tree): global.Tree = {
      if (configuration.ignoredFiles.exists(unit.source.path.matches)) {
        if (debug)
          reporter.echo(s"[debug] Skipping scapegoat [$unit]")
      } else {
        if (debug)
          reporter.echo(s"[debug] Scapegoat analysis [$unit] .....")
        val context = InspectionContext(global, feedback)
        activeInspections
          .filter(_.isEnabled)
          .foreach { inspection =>
            val inspector = inspection.inspector(context)
            inspector.postTyperTraverser.traverse(tree.asInstanceOf[inspector.context.global.Tree])
            inspector.postInspection()
          }
      }
      tree
    }
  }
}
