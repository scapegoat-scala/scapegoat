package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
class Reporter {

  val warnings = new ListBuffer[Warning]
  def warnings(level: Level): Seq[Warning] = warnings.filter(_.level == level)

  def warn(text: String, tree: scala.reflect.runtime.universe.Tree, level: Level): Unit = {
    warn(text, tree, level, None)
  }
  def warn(text: String, tree: scala.reflect.runtime.universe.Tree, level: Level, snippet: String): Unit = {
    warn(text, tree, level, Option(snippet))
  }
  def warn(text: String, tree: scala.reflect.runtime.universe.Tree, level: Level, snippet: Option[String]): Unit = {
    val sourceFile = normalizeSourceFile(tree.pos.source.file.path)
    warnings.append(Warning(text, tree.pos.line, level, sourceFile, snippet))
  }

  private def normalizeSourceFile(sourceFile: String): String = {
    val indexOf = sourceFile.indexOf("src/main/scala/")
    val packageAndFile = if (indexOf == -1) sourceFile else sourceFile.drop(indexOf).drop("src/main/scala/".length)
    packageAndFile.replace('/', '.').replace('\\','.')
  }
}

case class Warning(text: String, line: Int, level: Level, sourceFile: String, snippet: Option[String])
