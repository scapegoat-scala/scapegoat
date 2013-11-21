package org.scalescc.reporters

import scales._
import scala.xml.Node
import scales.MeasuredStatement
import scales.MeasuredClass
import scales.MeasuredMethod
import java.io.File
import org.apache.commons.io.FileUtils

/** @author Stephen Samuel */
object ScalesXmlWriter extends CoverageWriter {

  def write(coverage: Coverage, dir: File): Unit = {
    FileUtils.write(new File(dir.getAbsolutePath + "/scales.xml"), xml(coverage).toString())
  }

  def statement(stmt: MeasuredStatement): Node = {
    <statement package={stmt.location._package}
               class={stmt.location._class}
               method={stmt.location.method.orNull}
               start={stmt.start.toString}
               line={stmt.line.toString}
               invocation-count={stmt.count.toString}>
      {stmt.desc}
    </statement>
  }

  def method(method: MeasuredMethod): Node = {
    <method name={method.name}
            statement-rate={method.statementCoverage.toString}>
      <statements>
        {method.statements.map(statement)}
      </statements>
    </method>
  }

  def klass(klass: MeasuredClass): Node = {
    <class name={klass.name}
           statement-rate={klass.statementCoverage.toString}>
      <methods>
        {klass.methods.map(method)}
      </methods>
    </class>
  }

  def pack(pack: MeasuredPackage): Node = {
    <package name={pack.name}
             statement-rate={pack.statementCoverage.toString}>
      <classes>
        {pack.classes.map(klass)}
      </classes>
    </package>
  }

  def xml(coverage: Coverage): Node = {
    <scales statement-rate={coverage.statementCoverage.toString}
            version="1.0"
            timestamp={System.currentTimeMillis.toString}>
      <packages>
        {coverage.packages.map(pack)}
      </packages>
    </scales>
  }
}
