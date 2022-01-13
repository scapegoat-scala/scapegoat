package com.sksamuel.scapegoat

import scala.collection.mutable.ListBuffer
import scala.reflect.internal.util.Position
import scala.tools.nsc.reporters.Reporter

/**
 * @author
 *   Stephen Samuel
 */
class Feedback(
  reporter: Reporter,
  configuration: Configuration
) {

  private val warningsBuffer = new ListBuffer[Warning]

  def warnings: Seq[Warning] = warningsBuffer.toSeq
  def warningsWithMinimalLevel: Seq[Warning] =
    warnings.filter(_.hasMinimalLevelOf(configuration.minimalLevel))
  def shouldPrint(warning: Warning): Boolean =
    configuration.consoleOutput && warning.hasMinimalLevelOf(configuration.minimalLevel)

  def infos: Seq[Warning] = warnings(Levels.Info)
  def errors: Seq[Warning] = warnings(Levels.Error)
  def warns: Seq[Warning] = warnings(Levels.Warning)
  def warnings(level: Level): Seq[Warning] = warnings.filter(_.level == level)

  def warn(
    pos: Position,
    inspection: Inspection,
    snippet: Option[String]          = None,
    adhocExplanation: Option[String] = None
  ): Unit = {
    val level = inspection.defaultLevel
    val text = inspection.text
    val name = inspection.name
    val explanation = adhocExplanation.getOrElse(inspection.explanation)
    val adjustedLevel = (
      configuration.overrideLevels.get("all"),
      configuration.overrideLevels.get(inspection.getClass.getSimpleName)
    ) match {
      case (Some(l), _)    => l
      case (None, Some(l)) => l
      case _               => level
    }

    val sourceFileFull = pos.source.file.path
    val sourceFileNormalized = normalizeSourceFile(sourceFileFull)
    val warning = Warning(
      text = text,
      line = pos.line,
      level = adjustedLevel,
      sourceFileFull = sourceFileFull,
      sourceFileNormalized = sourceFileNormalized,
      snippet = snippet,
      explanation = explanation,
      inspection = inspection.getClass.getCanonicalName
    )
    warningsBuffer.append(warning)

    if (shouldPrint(warning)) {
      val snippetText = snippet.fold("")("\n  " + _ + "\n")
      val report = s"""[scapegoat] [$name] $text
                      |  $explanation$snippetText""".stripMargin

      adjustedLevel match {
        case Levels.Error   => reporter.error(pos, report)
        case Levels.Warning => reporter.warning(pos, report)
        case Levels.Info    => reporter.info(pos, report, force = false)
      }
    }
  }

  private def normalizeSourceFile(sourceFile: String): String = {
    val sourcePrefix = configuration.sourcePrefix
    val indexOf = sourceFile.indexOf(sourcePrefix)
    val fullPrefix = if (sourcePrefix.endsWith("/")) sourcePrefix else s"$sourcePrefix/"
    val packageAndFile = if (indexOf == -1) sourceFile else sourceFile.drop(indexOf).drop(fullPrefix.length)
    packageAndFile.replace('/', '.').replace('\\', '.')
  }
}

final case class Warning(
  text: String,
  line: Int,
  level: Level,
  sourceFileFull: String,
  sourceFileNormalized: String,
  snippet: Option[String],
  explanation: String,
  inspection: String
) {
  def hasMinimalLevelOf(minimalLevel: Level): Boolean = {
    minimalLevel match {
      case Levels.Info    => true
      case Levels.Warning => this.level == Levels.Warning || this.level == Levels.Error
      case Levels.Error   => this.level == Levels.Error
    }
  }
}
