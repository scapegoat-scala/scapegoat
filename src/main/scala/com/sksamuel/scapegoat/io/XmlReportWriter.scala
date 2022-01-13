package com.sksamuel.scapegoat.io

import scala.xml.{Elem, Node}

import com.sksamuel.scapegoat.{Feedback, Warning}

/**
 * @author
 *   Stephen Samuel
 */
object XmlReportWriter extends ReportWriter {

  override protected val fileName = "scapegoat.xml"

  private def toXML(feedback: Feedback): Node = {
    <scapegoat count={feedback.warnings.size.toString} warns={feedback.warns.size.toString} errors={
      feedback.errors.size.toString
    } infos={feedback.infos.size.toString}>
      {feedback.warningsWithMinimalLevel.map(warning2xml)}
    </scapegoat>
  }

  private def warning2xml(warning: Warning): Elem =
    <warning line={warning.line.toString} text={warning.text} snippet={warning.snippet.orNull} explanation={
      warning.explanation
    } level={warning.level.toString} file={warning.sourceFileNormalized} inspection={warning.inspection}/>

  override protected def generate(feedback: Feedback): String = toXML(feedback).toString()
}
