package com.sksamuel.scapegoat.io

import com.sksamuel.scapegoat.{ Warning, Feedback }

import scala.collection.mutable.ListBuffer
import scala.xml.Node

/** @author Eugene Sypachev (Axblade) */
object ScalastyleReportWriter {

  private val checkstyleVersion = "5.0"
  private val scapegoat = "scapegoat"

  def toXML(feedback: Feedback): Node = {
    <checkstyle version={ checkstyleVersion } generatedBy={ scapegoat }>
      { feedback.warnings.groupBy(_.sourceFileFull).map(fileToXml) }
    </checkstyle>
  }

  private def fileToXml(fileWarningMapEntry: (String, ListBuffer[Warning])) = {
    val (file, warnings) = fileWarningMapEntry
    <file name={ file }>
      { warnings.map(warningToXml) }
    </file>
  }

  private def warningToXml(warning: Warning) = {
    <error line={ warning.line.toString } message={ warning.text } severity={ warning.level.toString } source={ warning.inspection } snippet={ warning.snippet.orNull }></error>
  }

}
