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
  val component = new ScapegoatComponent(global, ScapegoatConfig.inspections)
  override val components: List[PluginComponent] = List(component)

  override def init(options: List[String], error: String => Unit): Boolean = {
    forProperty("disabledInspections:") match {
      case Some(option) =>
        component.disabledInspections = option.drop("disabledInspections:".length).split(':').toList
      case _ =>
    }
    forProperty("enabledInspections:") match {
      case Some(option) =>
        component.enabledInspections = option.drop("enabledInspections:".length).split(':').toList
      case _ =>
    }
    forProperty("consoleOutput:") match {
      case Some(option) => component.consoleOutput = option.drop("consoleOutput:".length).toBoolean
      case _            =>
    }
    forProperty("ignoredFiles:") match {
      case Some(option) => component.ignoredFiles = option.drop("ignoredFiles:".length).split(':').toList
      case _            =>
    }
    for (verbose <- forProperty("verbose:"))
      component.verbose = verbose.drop("verbose:".length).toBoolean
    forProperty("customInspectors:") match {
      case Some(option) =>
        component.customInpections = option
          .drop("customInspectors:".length)
          .split(':')
          .toSeq
          .map(inspection =>
            Class.forName(inspection).getConstructor().newInstance().asInstanceOf[Inspection]
          )
      case _ =>
    }
    forProperty("reports:") match {
      case Some(option) =>
        option
          .drop("reports:".length)
          .split(':')
          .toSeq
          .foreach {
            case "xml"        => component.disableXML = false
            case "html"       => component.disableHTML = false
            case "scalastyle" => component.disableScalastyleXML = false
            case "markdown"   => component.disableMarkdown = false
            case "all" =>
              component.disableXML = false
              component.disableHTML = false
              component.disableScalastyleXML = false
              component.disableMarkdown = false
            case "none" =>
              component.disableXML = true
              component.disableHTML = true
              component.disableScalastyleXML = true
              component.disableMarkdown = true
            case _ =>
          }
      case None =>
        component.disableXML = false
        component.disableHTML = false
        component.disableScalastyleXML = false
    }
    forProperty("overrideLevels:") foreach { option =>
      component.feedback.levelOverridesByInspectionSimpleName = option
        .drop("overrideLevels:".length)
        .split(":")
        .map { nameLevel =>
          nameLevel.split("=") match {
            case Array(insp, level) => insp -> Levels.fromName(level)
            case _ =>
              throw new IllegalArgumentException(
                s"Malformed argument to 'overrideLevels': '$nameLevel'. " +
                "Expecting 'name=level' where 'name' is the simple name of " +
                "an inspection and 'level' is the simple name of a " +
                "com.sksamuel.scapegoat.Level constant, e.g. 'Warning'."
              )
          }
        }
        .toMap
    }
    forProperty("sourcePrefix:") match {
      case Some(option) =>
        component.sourcePrefix = option.drop("sourcePrefix:".length)
      case None => component.sourcePrefix = "src/main/scala/"
    }
    forProperty("minimalLevel:") match {
      case Some(level) =>
        component.minimalLevel = Levels.fromName(level)
      case None => component.minimalLevel = Levels.Info
    }
    forProperty("dataDir:") match {
      case Some(option) =>
        component.dataDir = new File(option.drop("dataDir:".length))
        true
      case None =>
        error("-P:scapegoat:dataDir not specified")
        false
    }
  }

  override val optionsHelp: Option[String] = Some(
    Seq(
      "-P:scapegoat:dataDir:<pathtodatadir>                 where the report should be written",
      "-P:scapegoat:disabledInspections:<listofinspections> colon separated list of disabled inspections (defauls to none)",
      "-P:scapegoat:enabledInspections:<listofinspections>  colon separated list of enabled inspections (defaults to all)",
      "-P:scapegoat:customInspectors:<listofinspections>    colon separated list of custom inspections",
      "-P:scapegoat:ignoredFiles:<patterns>                 colon separated list of regexes to match ",
      "                                                     files to ignore.",
      "-P:scapegoat:verbose:<boolean>                       enable/disable verbose console messages",
      "-P:scapegoat:consoleOutput:<boolean>                 enable/disable console report output",
      "-P:scapegoat:reports:<reports>                       colon separated list of reports to generate.",
      "                                                     Valid options are `xml', `html', `scalastyle', 'markdown',",
      "                                                     or `all'. Use `none' to disable reports.",
      "-P:scapegoat:overrideLevels:<levels>                 override the built in warning levels, e.g. to",
      "                                                     downgrade a Error to a Warning.",
      "                                                     <levels> should be a colon separated list of name=level",
      "                                                     settings, where 'name' is the simple name of an inspection",
      "                                                     and 'level' is the simple name of a",
      "                                                     com.sksamuel.scapegoat.Level constant, e.g. 'Warning'.",
      "                                                     You can use 'all' for inspection name to operate on all inspections.",
      "-P:scapegoat:sourcePrefix:<prefix>                   overrides source prefix if it differs from src/main/scala",
      "                                                     for ex., in Play applications where sources are in app/ folder",
      "-P:scapegoat:minimalWarnLevel:<level>                provides minimal level of triggered inspections,",
      "                                                     that will be shown in a report.",
      "                                                     'level' is the simple name of a",
      "                                                     com.sksamuel.scapegoat.Level constant, e.g. 'Warning'."
    ).mkString("\n")
  )

  private def forProperty(name: String): Option[String] =
    options.find(_.startsWith(name))
}

class ScapegoatComponent(val global: Global, inspections: Seq[Inspection])
    extends PluginComponent
    with TypingTransformers
    with Transform {

  require(inspections != null)

  import global._

  var dataDir: File = new File(".")
  var disabledInspections: List[String] = Nil
  var enabledInspections: List[String] = Nil
  var ignoredFiles: List[String] = Nil
  var consoleOutput: Boolean = true
  var verbose: Boolean = false
  val debug: Boolean = false
  var summary: Boolean = true
  var disableXML = true
  var disableHTML = true
  var disableScalastyleXML = true
  var disableMarkdown = true
  var customInpections: Seq[Inspection] = Nil
  var sourcePrefix = "src/main/scala/"
  var minimalLevel: Level = Levels.Info

  private val count = new AtomicInteger(0)

  override val phaseName: String = "scapegoat"
  override val runsAfter: List[String] = List("typer")
  override val runsBefore = List[String]("patmat")

  def disableAll: Boolean = disabledInspections.exists(_.compareToIgnoreCase("all") == 0)

  def activeInspections: Seq[Inspection] = {
    if (enabledInspections.isEmpty)
      (inspections ++ customInpections)
        .filterNot(inspection => disabledInspections.contains(inspection.getClass.getSimpleName))
    else
      (inspections ++ customInpections)
        .filter(inspection => enabledInspections.contains(inspection.getClass.getSimpleName))
  }
  lazy val feedback = new Feedback(consoleOutput, global.reporter, sourcePrefix, minimalLevel)

  def writeReport(isDisabled: Boolean, reportName: String, writer: (File, Feedback) => File): Unit = {
    if (!isDisabled) {
      val output = writer(dataDir, feedback)
      if (verbose)
        reporter.echo(s"[info] [scapegoat] Written $reportName report [$output]")
    }
  }

  override def newPhase(prev: scala.tools.nsc.Phase): Phase =
    new Phase(prev) {
      override def run(): Unit = {
        if (disableAll)
          reporter.echo("[info] [scapegoat] All inspections disabled")
        else {
          reporter.echo(s"[info] [scapegoat] ${activeInspections.size} activated inspections")
          if (verbose)
            if (ignoredFiles.nonEmpty)
              reporter.echo(s"[info] [scapegoat] $ignoredFiles ignored file patterns")
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

          writeReport(disableHTML, "HTML", IOUtils.writeHTMLReport)
          writeReport(disableXML, "XML", IOUtils.writeXMLReport)
          writeReport(disableScalastyleXML, "Scalastyle XML", IOUtils.writeScalastyleReport)
          writeReport(disableMarkdown, "Markdown", IOUtils.writeMarkdownReport)
        }
      }
    }

  protected def newTransformer(unit: CompilationUnit): Transformer = {
    count.incrementAndGet()
    new Transformer(unit)
  }

  class Transformer(unit: global.CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: global.Tree): global.Tree = {
      if (ignoredFiles.exists(unit.source.path.matches)) {
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
