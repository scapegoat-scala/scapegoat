package com.sksamuel.scapegoat

import java.io.File

case class Configuration(
  dataDir: Option[File],
  disabledInspections: List[String],
  enabledInspections: List[String],
  ignoredFiles: List[String],
  consoleOutput: Boolean,
  verbose: Boolean,
  disableXML: Boolean,
  disableHTML: Boolean,
  disableScalastyleXML: Boolean,
  disableMarkdown: Boolean,
  customInspections: Seq[Inspection],
  sourcePrefix: String,
  minimalLevel: Level,
  levelOverridesByInspectionSimpleName: Map[String, Level]
)
