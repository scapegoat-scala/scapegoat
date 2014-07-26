package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
class Reporter {
  val warnings = new ListBuffer[Warning]
  def warn(text: String, tree: scala.reflect.runtime.universe.Tree, level: Level): Unit = {
    warn(text, tree, level, None)
  }
  def warn(text: String, tree: scala.reflect.runtime.universe.Tree, level: Level, snippet: String): Unit = {
    warn(text, tree, level, Option(snippet))
  }
  def warn(text: String, tree: scala.reflect.runtime.universe.Tree, level: Level, snippet: Option[String]): Unit = {
    warnings.append(Warning(text, tree.pos.line, level, tree.pos.source.file.path, snippet))
  }
  def warnings(level: Level): Seq[Warning] = warnings.filter(_.level == level)
}

case class Warning(text: String, line: Int, level: Level, sourceFile: String, snippet: Option[String])
