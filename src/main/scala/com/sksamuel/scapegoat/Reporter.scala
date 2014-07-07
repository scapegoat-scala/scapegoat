package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
class Reporter {
  val warnings = new ListBuffer[String]
  def warn(text: String): Unit = {
    warnings.append(text)
  }
}
