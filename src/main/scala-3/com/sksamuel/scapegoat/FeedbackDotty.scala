package com.sksamuel.scapegoat

import dotty.tools.dotc.interfaces.SourceFile
import dotty.tools.dotc.util.SrcPos
import dotty.tools.dotc.interfaces.SourcePosition

class FeedbackDotty(configuration: Configuration) extends Feedback[SourcePosition](configuration) {

  protected def toSourcePath(pos: SourcePosition): String = pos.source().path()
  protected def toSourceLine(pos: SourcePosition): Int = pos.line()
  protected def report(pos: SourcePosition, level: Level, message: String): Unit = {
    println(s"[$level] $message")
  }

}
