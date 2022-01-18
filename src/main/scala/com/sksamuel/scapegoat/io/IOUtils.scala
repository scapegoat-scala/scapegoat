package com.sksamuel.scapegoat.io

import java.io.File

import com.sksamuel.scapegoat.Feedback

/**
 * @author
 *   Stephen Samuel
 */
object IOUtils {
  def writeHTMLReport(targetDir: File, reporter: Feedback): File =
    HtmlReportWriter.write(targetDir, reporter)

  def writeXMLReport(targetDir: File, reporter: Feedback): File =
    XmlReportWriter.write(targetDir, reporter)

  def writeScalastyleReport(targetDir: File, reporter: Feedback): File =
    ScalastyleReportWriter.write(targetDir, reporter)

  def writeMarkdownReport(targetDir: File, reporter: Feedback): File =
    MarkdownReportWriter.write(targetDir, reporter)
}
