package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
class Reporter {
  val warnings = new ListBuffer[Warning]
  def warn(text: String, line: Int): Unit = {
    warnings.append(Warning(text, line))
  }
}

case class Warning(text: String, line: Int)
