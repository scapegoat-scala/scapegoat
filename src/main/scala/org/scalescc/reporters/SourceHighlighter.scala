package org.scalescc.reporters

import java.io.{FileReader, File}
import org.apache.commons.io.IOUtils
import scales._
import scala.collection.JavaConverters._
import scala.xml.{Unparsed, Node}
import scales.MeasuredStatement
import scales.MeasuredFile

/** @author Stephen Samuel */
class SourceHighlighter {

  def print(mfile: MeasuredFile): Node = {
    val file = new File(mfile.source)
    val lines = IOUtils.readLines(new FileReader(file)).asScala
    print(lines, mfile.statements)
  }

  def print(lines: Seq[String], statements: Iterable[MeasuredStatement]): Node = {
    var lineNumber = 0
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title id='title'>Scales Code Coverage</title>
        <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.3.0/pure-min.css"/>
      </head>
      <body style="font-family: monospace;">
        <table>
          {lines.map(_.replace(" ", "&nbsp;").replace("<", "&lt;").replace(">", "&gt;")).map(line => {
          lineNumber = lineNumber + 1
          <tr>
            <td>
              {lineNumber.toString}&nbsp; &nbsp;
            </td>
            <td>
              {Unparsed(line)}
            </td>
          </tr>
        })}
        </table>
        <br/>
        <br/>
        <br/>
        <br/>
      </body>
    </html>
  }

  def lineCss(status: LineStatus): String = status match {
    case Covered => "background: green"
    case MissingCoverage => "background: red"
    case NotInstrumented => "background: white"
  }
}
