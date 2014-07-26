package com.sksamuel.scapegoat

import java.io.File

import com.sksamuel.scapegoat.io.IOUtils

import scala.tools.nsc._
import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.transform.{Transform, TypingTransformers}

class ScapegoatPlugin(val global: Global) extends Plugin {

  override val name: String = "scapegoat"
  override val description: String = "scapegoat compiler plugin"
  val component = new ScapegoatComponent(global, ScapegoatConfig.inspections)
  override val components: List[PluginComponent] = List(component)

  override def init(options: List[String], error: String => Unit): Boolean = {
    options.find(_.startsWith("dataDir:")) match {
      case Some(option) =>
        component.dataDir = new File(option.drop("dataDir:".length))
        true
      case None =>
        error("-P:scapegoat:dataDir:<pathtodatadir> not specified")
        false
    }
  }

  override val optionsHelp: Option[String] = Some(Seq(
    "-P:scapegoat:dataDir:<pathtodatadir>    where the report should be written\n"
  ).mkString("\n"))
}

class ScapegoatComponent(val global: Global, inspections: Seq[Inspection])
  extends PluginComponent with TypingTransformers with Transform {
  require(inspections != null)

  import global._

  import scala.reflect.runtime.{universe => u}

  val reporter = new Reporter()
  var dataDir: File = _

  override val phaseName: String = "scapegoat"
  override val runsAfter: List[String] = List("typer")
  override val runsBefore = List[String]("patmat")

  override def newPhase(prev: scala.tools.nsc.Phase): Phase = new Phase(prev) {
    override def run(): Unit = {
      println("[info] [scapegoat]: Begin anaylsis...")
      super.run()
      val count = reporter.warnings.size
      println(s"[info] [scapegoat]: Anaylsis complete - $count warnings found.")
      // todo add in proper target dir
      IOUtils.writeHTMLReport(dataDir, reporter)
      IOUtils.writeXMLReport(dataDir, reporter)
    }
  }

  protected def newTransformer(unit: CompilationUnit): Transformer = new Transformer(unit)

  class Transformer(unit: global.CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: Tree) = {
      require(inspections != null)
      inspections.foreach(_.traverser(reporter).traverse(tree.asInstanceOf[u.Tree]))
      tree
    }
  }
}