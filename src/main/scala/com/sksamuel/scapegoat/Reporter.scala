package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
class Reporter {
  val warnings = new ListBuffer[Warning]
  def warn(text: String, line: Int): Unit = warn(text, line, None)
  def warn(text: String, line: Int, snippet: String): Unit = warn(text, line, Option(snippet))
  def warn(text: String, line: Int, snippet: Option[String]): Unit = {
    warnings.append(Warning(text, line, snippet))
  }
}

case class Warning(text: String, line: Int, snippet: Option[String])
