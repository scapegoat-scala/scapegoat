package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer
import scala.reflect.internal.util.Position
import scala.tools.nsc.reporters.Reporter

/** @author Stephen Samuel */
class Feedback(consoleOutput: Boolean, reporter: Reporter) {

  val warnings = new ListBuffer[Warning]

  var levelOverridesByInspectionSimpleName: Map[String, Level] = Map.empty

  def infos = warnings(Levels.Info)
  def errors = warnings(Levels.Error)
  def warns = warnings(Levels.Warning)
  def warnings(level: Level): Seq[Warning] = warnings.filter(_.level == level)

  def warn(text: String, pos: Position, level: Level, inspection: Inspection): Unit = {
    warn(text, pos, level, None, inspection)
  }

  def warn(text: String, pos: Position, level: Level, snippet: String, inspection: Inspection): Unit = {
    warn(text, pos, level, Option(snippet), inspection)
  }

  private def warn(text: String,
    pos: Position,
    level: Level,
    snippet: Option[String],
    inspection: Inspection): Unit = {

    val adjustedLevel = levelOverridesByInspectionSimpleName.get(inspection.getClass.getSimpleName) match {
      case Some(overrideLevel) => overrideLevel
      case None                => level
    }

    val sourceFileFull = pos.source.file.path
    val sourceFileNormalized = normalizeSourceFile(sourceFileFull)
    val warning = Warning(text, pos.line, adjustedLevel, sourceFileFull, sourceFileNormalized, snippet, inspection.getClass.getCanonicalName)
    warnings.append(warning)
    if (consoleOutput) {
      println(s"[${warning.level.toString.toLowerCase}] $sourceFileNormalized:${warning.line}: $text")
      snippet.foreach(s => println(s"          $s"))
      println()
    }

    adjustedLevel match {
      case Levels.Error   => reporter.error(pos, text)
      case Levels.Warning => reporter.warning(pos, text)
      case Levels.Info    => reporter.info(pos, text, force = false)
    }
  }

  private def normalizeSourceFile(sourceFile: String): String = {
    val indexOf = sourceFile.indexOf("src/main/scala/")
    val packageAndFile = if (indexOf == -1) sourceFile else sourceFile.drop(indexOf).drop("src/main/scala/".length)
    packageAndFile.replace('/', '.').replace('\\', '.')
  }
}

case class Warning(text: String,
  line: Int,
  level: Level,
  sourceFileFull: String,
  sourceFileNormalized: String,
  snippet: Option[String],
  inspection: String)
