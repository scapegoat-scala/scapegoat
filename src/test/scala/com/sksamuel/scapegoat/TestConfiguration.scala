package com.sksamuel.scapegoat

object TestConfiguration {

  def configuration = Configuration(
    dataDir = None,
    disabledInspections = List(),
    enabledInspections = List(),
    ignoredFiles = List(),
    consoleOutput = false,
    verbose = false,
    disableXML = true,
    disableHTML = true,
    disableScalastyleXML = true,
    disableMarkdown = true,
    customInspections = Seq(),
    sourcePrefix = "src/main/scala",
    minimalLevel = Levels.Info
  )
}
