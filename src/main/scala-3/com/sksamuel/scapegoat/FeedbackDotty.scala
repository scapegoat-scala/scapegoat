package com.sksamuel.scapegoat

import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Decorators.toMessage
import dotty.tools.dotc.reporting.Diagnostic
import dotty.tools.dotc.util.SourcePosition

class FeedbackDotty(configuration: Configuration)(using ctx: Context)
    extends Feedback[SourcePosition](configuration) {

  protected def toSourcePath(pos: SourcePosition): String = pos.source().path()
  protected def toSourceLine(pos: SourcePosition): Int = pos.line
  protected def report(pos: SourcePosition, level: Level, message: String): Unit = {
    level match {
      case Levels.Error   => ctx.reporter.report(Diagnostic.Error(message, pos))
      case Levels.Warning => ctx.reporter.report(Diagnostic.Warning(message.toMessage, pos))
      case Levels.Info    => ctx.reporter.report(Diagnostic.Info(message, pos))
      case Levels.Ignore  => ()
    }
  }

}
