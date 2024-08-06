package com.sksamuel.scapegoat.io

import java.io.File

import com.sksamuel.scapegoat.Feedback

/**
 * @author
 *   Stephen Samuel
 */
object IOUtils {
  def writeHTMLReport(targetDir: File, reporter: Feedback[_]): File =
    HtmlReportWriter.write(targetDir, reporter)

  def writeXMLReport(targetDir: File, reporter: Feedback[_]): File =
    XmlReportWriter.write(targetDir, reporter)

  def writeScalastyleReport(targetDir: File, reporter: Feedback[_]): File =
    ScalastyleReportWriter.write(targetDir, reporter)

  def writeMarkdownReport(targetDir: File, reporter: Feedback[_]): File =
    MarkdownReportWriter.write(targetDir, reporter)

  def writeGitlabCodeQualityReport(targetDir: File, reporter: Feedback[_]): File =
    GitlabCodeQualityReportWriter.write(targetDir, reporter)
}
