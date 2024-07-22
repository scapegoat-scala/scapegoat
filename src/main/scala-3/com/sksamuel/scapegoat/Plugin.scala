package com.sksamuel.scapegoat

import dotty.tools.dotc.plugins.StandardPlugin
import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.transform.PatternMatcher
import dotty.tools.dotc.transform.Erasure.Typer
import dotty.tools.dotc.transform.PostTyper

class ScapegoatPlugin extends StandardPlugin {

  override def name: String = "scapegoat"

  override def description: String = "scapegoat compiler plugin"

  override val optionsHelp: Option[String] = Some(Configuration.optionsHelp)

  override def init(options: List[String]): List[PluginPhase] = {
    val config = Configuration.fromPluginOptions(options)
    if (config.dataDir.isEmpty) {
      // TODO(johan): How are we supposed to report back errors on init?
      throw new IllegalArgumentException("-P:scapegoat:dataDir not specified")
    }

    new ScapegoatPhase(config, Inspections.inspections) :: Nil
  }

}

class ScapegoatPhase(var configuration: Configuration, override val inspections: Seq[Inspection])
    extends PluginPhase
    with ScapegoatBasePlugin {

  override def phaseName: String = "scapegoat"

  override val runsBefore: Set[String] = Set(PatternMatcher.name)

  override val runsAfter: Set[String] = Set(PostTyper.name)

}
