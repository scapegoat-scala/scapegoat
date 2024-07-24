package com.sksamuel.scapegoat

import dotty.tools.dotc.plugins.StandardPlugin
import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.transform.PatternMatcher
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.reporting.Reporter
import dotty.tools.dotc.reporting.Diagnostic.Info
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

  import tpd.*

  override def phaseName: String = "scapegoat"

  // TODO(johan): Where is the proper type exposing this name?
  override val runsAfter: Set[String] = Set("typer")

  override val runsBefore: Set[String] = Set(PatternMatcher.name)

  override def transformUnit(tree: tpd.Tree)(using ctx: Context): tpd.Tree = {
    val feedback = new FeedbackDotty(configuration)
    val inspections = activeInspections
    if (configuration.verbose) {
      ctx.reporter.report(Info(s"Running with ${inspections.size} active inspections", NoSourcePosition))
    }
    inspections.foreach { inspection =>
      inspection.inspect(feedback, tree)
    }
    tree
  }

}
