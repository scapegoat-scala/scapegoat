package com.sksamuel.scapegoat

import java.io.File

import com.sksamuel.scapegoat.io.IOUtils

import scala.tools.nsc._
import scala.tools.nsc.plugins.{ Plugin, PluginComponent }
import scala.tools.nsc.transform.{ Transform, TypingTransformers }

class ScapegoatPlugin(val global: Global) extends Plugin {

  override val name: String = "scapegoat"
  override val description: String = "scapegoat compiler plugin"
  val component = new ScapegoatComponent(global, ScapegoatConfig.inspections)
  override val components: List[PluginComponent] = List(component)

  override def init(options: List[String], error: String => Unit): Boolean = {
    options.find(_.startsWith("disabledInspections:")) match {
      case Some(option) => component.disabled = option.drop("disabledInspections:".length).split(':').toList
      case _            =>
    }
    options.find(_.startsWith("consoleOutput:")) match {
      case Some(option) => component.consoleOutput = option.drop("consoleOutput:".length).toBoolean
      case _            =>
    }
    options.find(_.startsWith("ignoredFiles:")) match {
      case Some(option) => component.ignoredFiles = option.drop("ignoredFiles:".length).split(':').toList
      case _            =>
    }
    for (verbose <- options.find(_.startsWith("verbose:"))) {
      component.verbose = verbose.drop("verbose:".length).toBoolean
    }
    options.find(_.startsWith("customInspectors:")) match {
      case Some(option) => component.customInpections =
        option.drop("customInspectors:".length)
          .split(':')
          .toSeq
          .map(inspection => Class.forName(inspection).newInstance.asInstanceOf[Inspection])
      case _ =>
    }
    options.find(_.startsWith("reports:")) match {
      case Some(option) =>
        option.drop("reports:".length)
          .split(':')
          .toSeq
          .foreach { r =>
            r match {
              case "xml"        => component.disableXML = false
              case "html"       => component.disableHTML = false
              case "scalastyle" => component.disableScalastyleXML = false
              case _            =>
            }
          }
      case None =>
        component.disableXML = false
        component.disableHTML = false
        component.disableScalastyleXML = false
    }
    options.find(_.startsWith("dataDir:")) match {
      case Some(option) =>
        component.dataDir = new File(option.drop("dataDir:".length))
        true
      case None =>
        error("-P:scapegoat:dataDir not specified")
        false
    }

  }

  override val optionsHelp: Option[String] = Some(Seq(
    "-P:scapegoat:dataDir:<pathtodatadir>    where the report should be written\n" +
      "-P:scapegoat:disabled:<listofinspections>    comma separated list of disabled inspections\n" +
      "-P:scapegoat:customInspectors:<listofinspections>    comma separated list of custom inspections\n").mkString("\n"))
}

class ScapegoatComponent(val global: Global, inspections: Seq[Inspection])
    extends PluginComponent with TypingTransformers with Transform {

  require(inspections != null)

  import global._

  var dataDir: File = new File(".")
  var disabled: List[String] = Nil
  var ignoredFiles: List[String] = Nil
  var consoleOutput: Boolean = false
  var verbose: Boolean = false
  var debug: Boolean = false
  var summary: Boolean = true
  var disableXML = true
  var disableHTML = true
  var disableScalastyleXML = true
  var customInpections: Seq[Inspection] = Nil

  override val phaseName: String = "scapegoat"
  override val runsAfter: List[String] = List("typer")
  override val runsBefore = List[String]("patmat")

  def disableAll = disabled.exists(_.compareToIgnoreCase("all") == 0)

  def activeInspections = (inspections ++ customInpections).filterNot(inspection => disabled.contains(inspection.getClass.getSimpleName))
  lazy val feedback = new Feedback(consoleOutput)

  override def newPhase(prev: scala.tools.nsc.Phase): Phase = new Phase(prev) {
    override def run(): Unit = {
      if (disableAll) {
        if (verbose) {
          println("[info] [scapegoat] All inspections disabled")
        }
      } else {
        if (verbose) {
          println(s"[info] [scapegoat] ${activeInspections.size} activated inspections")
          if (ignoredFiles.nonEmpty)
            println(s"[info] [scapegoat] $ignoredFiles ignored file patterns")
        }
        super.run()

        if (summary) {
          val errors = feedback.errors.size
          val warns = feedback.warns.size
          val infos = feedback.infos.size
          val level = if (errors > 0) "error" else if (warns > 0) "warn" else "info"
          println(s"[$level] [scapegoat] Analysis complete - $errors errors $warns warns $infos infos")
        }

        if (!disableHTML) {
          val html = IOUtils.writeHTMLReport(dataDir, feedback)
          if (verbose)
            println(s"[info] [scapegoat] Written HTML report [$html]")
        }
        if (!disableXML) {
          val xml = IOUtils.writeXMLReport(dataDir, feedback)
          if (verbose)
            println(s"[info] [scapegoat] Written XML report [$xml]")
        }
        if (!disableScalastyleXML) {
          val xml = IOUtils.writeScalastyleReport(dataDir, feedback)
          if (verbose)
            println(s"[info] [scapegoat] Written Scalastyle XML report [$xml]")
        }
      }
    }
  }

  protected def newTransformer(unit: CompilationUnit): Transformer = new Transformer(unit)

  class Transformer(unit: global.CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: global.Tree) = {
      if (ignoredFiles.exists(unit.source.path.matches)) {
        if (debug) {
          println(s"[debug] Skipping scapegoat [$unit]")
        }
      } else {
        if (debug) {
          println(s"[debug] Scapegoat analysis [$unit] .....")
        }
        val context = new InspectionContext(global, feedback)
        activeInspections.foreach(inspection => {
          val inspector = inspection.inspector(context)
          for (traverser <- inspector.postTyperTraverser)
            traverser.traverse(tree.asInstanceOf[inspector.context.global.Tree])
          inspector.postInspection()
        })
      }
      tree
    }
  }
}
