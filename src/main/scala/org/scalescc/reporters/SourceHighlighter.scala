package org.scalescc.reporters

import java.io.{FileReader, File}
import org.apache.commons.io.IOUtils
import scales._
import scala.collection.JavaConverters._
import scala.xml.Node
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
    <table>
      {lines.map(_.replace(" ", "&nbsp")).map(line => {
      lineNumber = lineNumber + 1
      <tr>
        <td>
          {lineNumber.toString}
        </td>
        <td>
          {line}
        </td>
      </tr>
    })}
    </table>
  }

  def lineCss(status: LineStatus): String = status match {
    case Covered => "background: green"
    case MissingCoverage => "background: red"
    case NotInstrumented => "background: white"
  }
}
