package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer
import scala.reflect.internal.util.Position
import scala.tools.nsc.reporters.Reporter

/** @author Stephen Samuel */
class Feedback(consoleOutput: Boolean, reporter: Reporter, sourcePrefix: String) {

  val warnings = new ListBuffer[Warning]

  var levelOverridesByInspectionSimpleName: Map[String, Level] = Map.empty

  def infos = warnings(Levels.Info)
  def errors = warnings(Levels.Error)
  def warns = warnings(Levels.Warning)
  def warnings(level: Level): Seq[Warning] = warnings.filter(_.level == level)

  def warn(pos: Position, inspection: Inspection, snippet: Option[String] = None): Unit = {
    val level = inspection.defaultLevel
    val text = inspection.text
    val snippetText = inspection.explanation.orElse(snippet)
    val adjustedLevel = levelOverridesByInspectionSimpleName.getOrElse(inspection.getClass.getSimpleName, level)

    val sourceFileFull = pos.source.file.path
    val sourceFileNormalized = normalizeSourceFile(sourceFileFull)
    val warning = Warning(text, pos.line, adjustedLevel, sourceFileFull, sourceFileNormalized, snippetText, inspection.getClass.getCanonicalName)
    warnings.append(warning)
    if (consoleOutput) {
      println(s"[${warning.level.toString.toLowerCase}] $sourceFileNormalized:${warning.line}: $text")
      snippetText.foreach(s => println(s"          $s"))
      println()
    }

    adjustedLevel match {
      case Levels.Error   => reporter.error(pos, text)
      case Levels.Warning => reporter.warning(pos, text)
      case Levels.Info    => reporter.info(pos, text, force = false)
    }
  }

  private def normalizeSourceFile(sourceFile: String): String = {
    val indexOf = sourceFile.indexOf(sourcePrefix)
    val fullPrefix = if (sourcePrefix.endsWith("/")) sourcePrefix else s"$sourcePrefix/"
    val packageAndFile = if (indexOf == -1) sourceFile else sourceFile.drop(indexOf).drop(fullPrefix.length)
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
