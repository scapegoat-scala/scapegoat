package com.sksamuel.scapegoat.io

import java.io.{ BufferedWriter, File, FileWriter }

import com.sksamuel.scapegoat.Feedback

/** @author Stephen Samuel */
object IOUtils {

  private val XmlFile = "scapegoat.xml"
  private val HtmlFile = "scapegoat.html"

  def serialize(file: File, str: String) = {
    val out = new BufferedWriter(new FileWriter(file))
    out.write(str)
    out.close()
  }

  def writeHTMLReport(targetDir: File, reporter: Feedback): File = {
    targetDir.mkdirs()
    val html = HtmlReportWriter.generate(reporter)
    val file = new File(targetDir.getAbsolutePath + "/" + HtmlFile)
    serialize(file, html.toString())
    file
  }

  def writeXMLReport(targetDir: File, reporter: Feedback): File = {
    targetDir.mkdirs()
    val html = XmlReportWriter.toXML(reporter)
    val file = new File(targetDir.getAbsolutePath + "/" + XmlFile)
    serialize(file, html.toString())
    file
  }

}
