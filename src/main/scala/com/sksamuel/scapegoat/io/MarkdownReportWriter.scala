package com.sksamuel.scapegoat.io

import com.sksamuel.scapegoat.{Feedback, Levels, Warning}

object MarkdownReportWriter extends ReportWriter {
  override protected def fileName: String = "scapegoat.md"

  override protected def generate(reporter: Feedback): String = {
    s"""# Scapegoat Inspections
       |
       |**Errors**: ${reporter.warnings(Levels.Error).size.toString}
       |
       |**Warnings**: ${reporter.warnings(Levels.Warning).size.toString}
       |
       |**Infos**: ${reporter.warnings(Levels.Info).size.toString}
       |
       |## Report
       |
       |${renderAll(reporter)}
       |""".stripMargin
  }

  private def renderAll(reporter: Feedback): String =
    reporter.warningsWithMinimalLevel.map(renderWarning).mkString("\n")

  private def renderWarning(warning: Warning): String = {
    val source = warning.sourceFileNormalized + ":" + warning.line
    val md =
      s"""### $source
         |
         |**Level**: ${warning.level.toString}
         |
         |**Inspection**: ${warning.inspection}
         |
         |${warning.text}
         |
         |${warning.explanation}
         |
         |${warning.snippet.map(snippet => s"\n```scala\n$snippet\n```").getOrElse("")}
         |""".stripMargin
    md
  }
}
