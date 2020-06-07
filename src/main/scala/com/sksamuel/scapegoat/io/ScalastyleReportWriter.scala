package com.sksamuel.scapegoat.io

import scala.xml.Node

import com.sksamuel.scapegoat.{Feedback, Warning}

object ScalastyleReportWriter extends ReportWriter {

  private val checkstyleVersion = "5.0"
  private val scapegoat = "scapegoat"

  protected val fileName = "scapegoat-scalastyle.xml"

  def generate(feedback: Feedback): String = {
    val xml = <checkstyle version={checkstyleVersion} generatedBy={scapegoat}>
      {feedback.warningsWithMinimalLevel.groupBy(_.sourceFileFull).map(fileToXml)}
    </checkstyle>
    xml.toString()
  }

  private def fileToXml(fileWarningMapEntry: (String, Seq[Warning])) = {
    val (file, warnings) = fileWarningMapEntry
    <file name={file}>
      {warnings.map(warningToXml)}
    </file>
  }

  private def warningToXml(warning: Warning) =
    <error line={warning.line.toString} message={warning.text} severity={warning.level.toString} source={
      warning.inspection
    } snippet={warning.snippet.orNull} explanation={warning.explanation}></error>

}
