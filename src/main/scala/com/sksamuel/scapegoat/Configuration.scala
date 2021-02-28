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

object Configuration {

  def fromPluginOptions(options: List[String], error: String => Unit): Configuration = {
    def fromProperty[T](propertyName: String, defaultValue: T)(fn: String => T): T = {
      options.find(_.startsWith(propertyName + ":")) match {
        case Some(property) =>
          val justTheValue = property.drop(propertyName.length + 1)
          fn(justTheValue)
        case None =>
          defaultValue
      }
    }

    val disabledInspections =
      fromProperty("disabledInspections", defaultValue = List.empty[String])(_.split(':').toList)
    val enabledInspections =
      fromProperty("enabledInspections", defaultValue = List.empty[String])(_.split(':').toList)
    val consoleOutput = fromProperty("consoleOutput", defaultValue = true)(_.toBoolean)
    val ignoredFiles =
      fromProperty("ignoredFiles", defaultValue = List.empty[String])(_.split(':').toList)
    val verbose = fromProperty("verbose", defaultValue = false)(_.toBoolean)

    val customInspections = fromProperty("customInspectors", defaultValue = Seq.empty[Inspection]) {
      _.split(':').toSeq
        .map(inspection => Class.forName(inspection).getConstructor().newInstance().asInstanceOf[Inspection])
    }
    val enabledReports = fromProperty("reports", defaultValue = Seq("all"))(_.split(':').toSeq)
    val disableXML = !(enabledReports.contains("xml") || enabledReports.contains("all"))
    val disableHTML = !(enabledReports.contains("html") || enabledReports.contains("all"))
    val disableScalastyleXML =
      !(enabledReports.contains("scalastyle") || enabledReports.contains("all"))
    val disableMarkdown = !(enabledReports.contains("markdown") || enabledReports.contains("all"))

    val levelOverridesByInspectionSimpleName =
      fromProperty("overrideLevels", defaultValue = Map.empty[String, Level]) {
        _.split(":").map { nameLevel =>
          nameLevel.split("=") match {
            case Array(insp, level) => insp -> Levels.fromName(level)
            case _ =>
              throw new IllegalArgumentException(
                s"Malformed argument to 'overrideLevels': '$nameLevel'. " +
                "Expecting 'name=level' where 'name' is the simple name of " +
                "an inspection and 'level' is the simple name of a " +
                "com.sksamuel.scapegoat.Level constant, e.g. 'Warning'."
              )
          }
        }.toMap
      }
    val sourcePrefix = fromProperty("sourcePrefix", defaultValue = "src/main/scala/")(x => x)
    val minimalLevel = fromProperty[Level]("minimalLevel", defaultValue = Levels.Info) { value =>
      Levels.fromName(value)
    }
    val dataDir = fromProperty[Option[File]](
      "dataDir",
      defaultValue = {
        error("-P:scapegoat:dataDir not specified")
        None
      }
    ) { value =>
      Some(new File(value))
    }

    Configuration(
      dataDir = dataDir,
      disabledInspections = disabledInspections,
      enabledInspections = enabledInspections,
      ignoredFiles = ignoredFiles,
      consoleOutput = consoleOutput,
      verbose = verbose,
      disableXML = disableXML,
      disableHTML = disableHTML,
      disableScalastyleXML = disableScalastyleXML,
      disableMarkdown = disableMarkdown,
      customInspections = customInspections,
      sourcePrefix = sourcePrefix,
      minimalLevel = minimalLevel,
      levelOverridesByInspectionSimpleName = levelOverridesByInspectionSimpleName
    )
  }

  val optionsHelp: String = {
    Seq(
      "-P:scapegoat:dataDir:<pathtodatadir>                 where the report should be written",
      "-P:scapegoat:disabledInspections:<listofinspections> colon separated list of disabled inspections (defaults to none)",
      "-P:scapegoat:enabledInspections:<listofinspections>  colon separated list of enabled inspections (defaults to all)",
      "-P:scapegoat:customInspectors:<listofinspections>    colon separated list of custom inspections",
      "-P:scapegoat:ignoredFiles:<patterns>                 colon separated list of regexes to match ",
      "                                                     files to ignore.",
      "-P:scapegoat:verbose:<boolean>                       enable/disable verbose console messages",
      "-P:scapegoat:consoleOutput:<boolean>                 enable/disable console report output",
      "-P:scapegoat:reports:<reports>                       colon separated list of reports to generate.",
      "                                                     Valid options are `xml', `html', `scalastyle', 'markdown',",
      "                                                     or `all'. Use `none' to disable reports.",
      "-P:scapegoat:overrideLevels:<levels>                 override the built in warning levels, e.g. to",
      "                                                     downgrade a Error to a Warning.",
      "                                                     <levels> should be a colon separated list of name=level",
      "                                                     settings, where 'name' is the simple name of an inspection",
      "                                                     and 'level' is the simple name of a",
      "                                                     com.sksamuel.scapegoat.Level constant, e.g. 'Warning'.",
      "                                                     You can use 'all' for inspection name to operate on all inspections.",
      "-P:scapegoat:sourcePrefix:<prefix>                   overrides source prefix if it differs from src/main/scala",
      "                                                     for ex., in Play applications where sources are in app/ folder",
      "-P:scapegoat:minimalWarnLevel:<level>                provides minimal level of triggered inspections,",
      "                                                     that will be shown in a report.",
      "                                                     'level' is the simple name of a",
      "                                                     com.sksamuel.scapegoat.Level constant, e.g. 'Warning'."
    ).mkString("\n")
  }
}
