package org.scalescc.reporters

import scales._
import scala.reflect.internal.util.SourceFile
import scala.xml.Node
import scales.MeasuredFile
import java.util.Date
import java.io.{FileWriter, BufferedWriter, File}

/** @author Stephen Samuel */
object ScalesHtmlWriter extends CoverageWriter {

  def write(coverage: Coverage, dir: File): Unit = {
    val indexFile = new File(dir.getAbsolutePath + "/index.html")
    val writer = new BufferedWriter(new FileWriter(indexFile))
    writer.append(xml(coverage).toString())
    writer.close()
  }

  def packages(coverage: Coverage): Node = {
    <ul>
      {coverage.packages.map(arg =>
      <li>
        {arg.name}{arg.statementCoverage.toString}
        %
      </li>)}
    </ul>
  }

  def risks(coverage: Coverage) = {
    <div id="risks">
      <div>Total 20 Project Risks</div>{coverage.risks(20).map(arg => <div>
      {arg.name}
    </div>)}
    </div>
  }

  def packages2(coverage: Coverage) = {
    val rows = coverage.packages.map(arg => {
      <tr>
        <td>
          {arg.name}
        </td>
        <td>
          {arg.invokedClasses.toString}
          /
          {arg.classCount}
          (
          {arg.classCoverage.toString}
          %)
        </td>
        <td>
          {arg.invokedStatements.toString}
          /
          {arg.statementCount}
          (
          {arg.statementCoverage.toString}
          %)
        </td>
      </tr>
    })
    <table>
      {rows}
    </table>
  }

  def overview(coverage: Coverage) = {
    <table>
      <caption>Statistics generated at
        {new Date().toString}
      </caption>
      <tr>
        <td>Lines of code:</td>
        <td>
          {coverage.loc.toString}
        </td>
        <td>Statements:</td>
        <td>
          {coverage.statementCount.toString}
        </td>
        <td>Clases per package:</td>
        <td>
          {coverage.avgClassesPerPackage.toString}
        </td>
        <td>Methods per class:</td>
        <td>
          {coverage.avgMethodsPerClass.toString}
        </td>
      </tr>
      <tr>
        <td>Non comment lines of code:</td>
        <td>
          {coverage.ncloc.toString}
        </td>
        <td>Packages:</td>
        <td>
          {coverage.classCount.toString}
        </td>
        <td>Classes:</td>
        <td>
          {coverage.packageCount.toString}
        </td>
        <td>Methods:</td>
        <td>
          {coverage.methodCount.toString}
        </td>
      </tr>
    </table>
  }

  def writeIndex(coverage: Coverage) {
    val data = <html>
      <head>
        <title>Scales Code Coverage Overview</title>
        <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.2.0/pure-nr-min.css"/>
      </head>
      <body>
        <h1>Scales Code Coverage</h1>{overview(coverage)}{risks(coverage)}{packages(coverage)}
      </body>
    </html>
    IOUtils.write("index.html", data.toString())
  }

  def lines(source: SourceFile): Seq[String] = new String(source.content).split("\n")

  def table(file: MeasuredFile): Seq[Node] = {
    var lineNumber = 0
    lines(file.source).map(line => {
      lineNumber = lineNumber + 1
      val status = file.lineStatus(lineNumber)
      val css = lineCss(status)
      <tr>
        <td>
          {lineNumber.toString}
        </td>
        <td style={css}>

        </td>
      </tr>
    })
  }

  def lineCss(status: LineStatus): String = status match {
    case Covered => "background: green"
    case MissingCoverage => "background: red"
    case NotInstrumented => "background: white"
  }

  def html(file: MeasuredFile) =
    <html>
      <head>
        <title>
          {file.source}
        </title>
        <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.2.0/pure-nr-min.css"/>
      </head>
      <body>
        <h1>
          Filename:
          {file.source}
        </h1>
        <div>Statement Coverage:
          {file.invokedStatements.toString}
          /
          {file.statementCount.toString}{file.statementCoverage.toString}
          %
        </div>
        <table>
          {table(file)}
        </table>
      </body>
    </html>
}

