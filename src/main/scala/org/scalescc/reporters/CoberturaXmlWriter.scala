package org.scalescc.reporters

import scales.{MeasuredMethod, MeasuredClass, MeasuredPackage, Coverage}
import scala.xml.Node
import java.io.{BufferedWriter, FileWriter, File}

/** @author Stephen Samuel */
object CoberturaXmlWriter extends CoverageWriter {

  def write(coverage: Coverage, dir: File): Unit = {
    val file = new File(dir.getAbsolutePath + "/cobertura.xml")
    val writer = new BufferedWriter(new FileWriter(file))
    writer.append(xml(coverage).toString())
    writer.close()
  }

  def method(method: MeasuredMethod): Node = {
    <method name={method.name}
            signature="()V"
            line-rate={method.statementCoverage.toString}
            branch-rate="0">
      <lines>
        {method.statements.map(stmt => <line number={stmt.line} hits={stmt.count} branch="false"/>)}
      </lines>
    </method>
  }

  def klass(klass: MeasuredClass): Node = {
    <class name={klass.name}
           filename="notimpl.java"
           line-rate={klass.statementCoverage.toString}
           branch-rate="0" complexity="0">
      <methods>
        {klass.methods.map(method)}
      </methods>
    </class>
  }

  def pack(pack: MeasuredPackage): Node = {
    <package name={pack.name}
             line-rate={pack.statementCoverage.toString}
             branch-rate="0"
             complexity="0">
      <classes>
        {pack.classes.map(klass)}
      </classes>
    </package>
  }

  def xml(coverage: Coverage): Node = {
    <coverage line-rate={coverage.statementCoverage.toString}
              branch-rate="0"
              version="1.0"
              timestamp={System.currentTimeMillis.toString}>
      <sources>
        <source>C:/local/mvn-coverage-example/src/main/java</source>
      </sources>
      <packages>
        {coverage.packages.map(pack)}
      </packages>
    </coverage>
  }
}
