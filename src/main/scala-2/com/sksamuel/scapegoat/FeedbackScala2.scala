package com.sksamuel.scapegoat

import scala.tools.nsc.reporters.Reporter
import scala.reflect.internal.util.Position

class FeedbackScala2(
  reporter: Reporter,
  configuration: Configuration
) extends Feedback[Position](configuration) {

  protected def toSourcePath(pos: Position): String = pos.source.file.path
  protected def toSourceLine(pos: Position): Int = pos.line

  override protected def report(pos: Position, level: Level, message: String): Unit = {
    level match {
      case Levels.Error   => reporter.error(pos, message)
      case Levels.Warning => reporter.warning(pos, message)
      case Levels.Info    => reporter.echo(pos, message)
      case Levels.Ignore  => ()
    }
  }
}
