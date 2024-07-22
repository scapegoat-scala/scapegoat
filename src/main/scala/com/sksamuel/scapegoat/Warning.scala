package com.sksamuel.scapegoat

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
      case Levels.Ignore  => throw new IllegalArgumentException("Ignore cannot be minimal level")
      case Levels.Info    => this.level.higherOrEqualTo(Levels.Info)
      case Levels.Warning => this.level.higherOrEqualTo(Levels.Warning)
      case Levels.Error   => this.level.higherOrEqualTo(Levels.Error)
    }
  }
}
