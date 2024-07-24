package com.sksamuel.scapegoat

import dotty.tools.dotc.interfaces.SourceFile
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.interfaces.SourcePosition

abstract class Inspection(
  val text: String,
  val defaultLevel: Level,
  val description: String,
  val explanation: String
) extends InspectionBase {

  val self: Inspection = this

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using Context): Unit

}
