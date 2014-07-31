package com.sksamuel.scapegoat.io

import com.sksamuel.scapegoat.{Feedback, Warning}

import scala.xml.Node

/** @author Stephen Samuel */
object XmlReportWriter {

  def toXML(reporter: Feedback): Node = {
    <scapegoat count={reporter.warnings.size.toString}>
      {reporter.warnings.map(warning2xml)}
    </scapegoat>
  }

  private def warning2xml(warning: Warning) = {
      <warning line={warning.line.toString}
               text={warning.text}
               snippet={warning.snippet.orNull}
               level={warning.level.toString}
               file={warning.sourceFile}/>
  }
}
