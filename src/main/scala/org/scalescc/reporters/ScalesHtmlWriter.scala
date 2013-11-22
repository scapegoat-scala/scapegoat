package org.scalescc.reporters

import scales._
import scala.reflect.internal.util.SourceFile
import scala.xml.Node
import scales.MeasuredFile
import java.util.Date
import java.io.File
import org.apache.commons.io.FileUtils

/** @author Stephen Samuel */
object ScalesHtmlWriter extends CoverageWriter {

  def write(coverage: Coverage, dir: File): Unit = {
    val indexFile = new File(dir.getAbsolutePath + "/index.html")
    val packageFile = new File(dir.getAbsolutePath + "/packages.html")
    val overviewFile = new File(dir.getAbsolutePath + "/overview.html")

    FileUtils.copyInputStreamToFile(getClass.getResourceAsStream("/org/scalescc/reporters/index.html"), indexFile)
    FileUtils.write(packageFile, packages(coverage).toString())
    FileUtils.write(overviewFile, overview(coverage).toString())

    coverage.packages.foreach(write(_, dir))
    coverage.classes.foreach(write(_, dir))
  }

  def write(pack: MeasuredPackage, dir: File) {
    val file = new File(dir.getAbsolutePath + "/" + pack.name.replace('.', '/') + pack.name.split('.').last + ".html")
    file.getParentFile.mkdirs()
    FileUtils.write(file, _package(pack).toString())
  }

  def write(klass: MeasuredClass, dir: File) {
    val file = new File(dir.getAbsolutePath + "/" + klass.name.replace('.', '/') + ".html")
    file.getParentFile.mkdirs()
    FileUtils.write(file, _class(klass).toString())
  }

  def _package(pack: MeasuredPackage): Node = {
    <table class="pure-table pure-table-bordered pure-table-striped">
      <thead>
        <tr>
          <th>Class</th>
          <th>Lines</th>
          <th>Methods</th>
          <th>Statements</th>
          <th>Statement Coverage</th>
          <th>Branch Coverage</th>
          <th>Complexity</th>
        </tr>

      </thead>
      <tbody>
        {pack.classes.map(_class)}
      </tbody>
    </table>
  }

  def _class(klass: MeasuredClass): Node = {
    <tr>
      <td>
        <a href={klass.name.split('.').last + ".html"}>
          {klass.name}
        </a>
      </td>
      <td>
        {klass.loc.toString}
      </td>
      <td>
        {klass.methodCount.toString}
      </td>
      <td>
        {klass.statementCount.toString}
      </td>
      <td>
        {klass.statementCoverageFormatted}
      </td>
      <td>tbc</td>
      <td>tbc</td>
    </tr>
  }

  def packages(coverage: Coverage): Node = {
    <ul>
      <li>
        <a href="overview.html" target="mainFrame">
          All packages
        </a>{coverage.statementCoverageFormatted}
        %
      </li>{coverage.packages.map(arg =>
      <li>
        <a href={arg.name.replace('.', '/') + ".html"} target="mainFrame">
          {arg.name}
        </a>{arg.statementCoverageFormatted}
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
          {arg.statementCoverageFormatted}
          %)
        </td>
      </tr>
    })
    <table>
      {rows}
    </table>
  }

  def overview(coverage: Coverage): Node = {
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
          {coverage.statementCount}
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
        <td>to be completed</td>
        <td>

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
}

