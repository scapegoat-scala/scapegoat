package com.sk.samuel.scapegoat

import scala.tools.nsc.reporters.Reporter

class FeedbackScala2(
  reporter: Reporter,
  configuration: Configuration
  ) extends Feedback[Position](configuration) {

  override protected def report(pos: T, level: Level, message: String): Unit = {
      level match {
        case Levels.Error   => reporter.error(pos, report)
        case Levels.Warning => reporter.warning(pos, report)
        case Levels.Info    => reporter.echo(pos, report)
        case Levels.Ignore  => ()
      }
  }
}

