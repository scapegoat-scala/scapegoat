package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
class Reporter {
  val warnings = new ListBuffer[Warning]
  def warn(text: String, line: Int, level: Level): Unit = warn(text, line, level, None)
  def warn(text: String, line: Int, level: Level, snippet: String): Unit = warn(text, line, level, Option(snippet))
  def warn(text: String, line: Int, level: Level, snippet: Option[String]): Unit = {
    warnings.append(Warning(text, line, level, snippet))
  }
  def warnings(level: Level): Seq[Warning] = warnings.filter(_.level == level)
}

case class Warning(text: String, line: Int, level: Level, snippet: Option[String])
