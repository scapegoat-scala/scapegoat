package com.sksamuel.scapegoat

object TestConfiguration {

  def configuration = Configuration(
    dataDir = None,
    disabledInspections = List(),
    enabledInspections = List(),
    ignoredFiles = List(),
    consoleOutput = false,
    verbose = false,
    reports = Reports(
      disableXML = true,
      disableHTML = true,
      disableScalastyleXML = true,
      disableMarkdown = true,
      disableGitlabCodeQuality = true
    ),
    customInspectors = Seq(),
    sourcePrefix = "src/main/scala",
    minimalLevel = Levels.Info,
    overrideLevels = Map.empty[String, Level]
  )
}
