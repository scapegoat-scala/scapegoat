package org.scalescc.reporters

import java.io.{FileInputStream, File}
import org.apache.commons.io.IOUtils
import scala.xml.{Unparsed, Node}
import scales.MeasuredFile

/** @author Stephen Samuel */
class SourceHighlighter {

  def print(mfile: MeasuredFile): Node = {
    val file = new File(mfile.source)
    val source = IOUtils.toString(new FileInputStream(file), "UTF-8")
    val ranges = mfile.invokedStatements.map(arg => arg.start to arg.end)
    val intersection = collapse(ranges)
    val highlighted = highlight(source, intersection)
    val lines = highlighted.split(System.getProperty("line.separator"))
    print(lines)
  }

  def collapse(ranges: Iterable[Range]): Seq[Range] = {
    // sorting the list puts overlapping ranges adjacent to one another in the list
    // foldLeft runs a function on successive elements. it's a great way to process
    // a list when the results are not a 1:1 mapping.
    ranges.toSeq.sortBy(_.start).foldLeft(List.empty[Range]) {
      (acc, r) =>
        acc match {
          case head :: tail if head.start <= r.start && r.end <= head.end =>
            // r completely contained; drop it
            head :: tail
          case head :: tail if head.contains(r.start) =>
            // partial overlap; expand head to include both head and r
            Range(head.start, r.end) :: tail
          case _ =>
            r :: acc
        }
    }.reverse
  }

  def highlight(source: String, statements: Seq[Range]) = {
    var offset = 0
    val opening = "[invoked]"
    val closing = "[/invoked]"
    statements.foldLeft(source)((a, b) => {
      val adjusted = new Range(b.start + offset, b.end + offset, 1)
      val before = a.take(adjusted.start)
      val middle = a.drop(adjusted.start).take(adjusted.length)
      val after = a.drop(adjusted.end)
      offset = offset + opening.length + closing.length
      before + opening + middle + closing + after
    })
  }

  def print(lines: Seq[String]): Node = {
    var lineNumber = 0
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title id='title'>Scales Code Coverage</title>
        <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.3.0/pure-min.css"/>
      </head>
      <body style="font-family: monospace;">
        <table>
          {lines.map(_
          .replace(" ", "&nbsp;")
          .replace("<", "&lt;")
          .replace(">", "&gt;")
          .replace("[invoked]", "<span style='background: #AEF1AE'>")
          .replace("[/invoked]", "</span>")).map(line => {
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

  def statementCss(status: StatementStatus): String = status match {
    case Covered => "background: green"
    case MissingCoverage => "background: red"
    case NotInstrumented => "background: white"
  }
}
