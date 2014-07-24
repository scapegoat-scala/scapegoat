package com.sksamuel.scapegoat.io

import java.io.{BufferedWriter, File, FileWriter}

import com.sksamuel.scapegoat.Warning

/** @author Stephen Samuel */
object IOUtils {

  private val WarningsFile = "scapegoat-warnings.xml"

  def serialize(warnings: Seq[Warning], targetDir: File) = {
    val file = new File(targetDir.getAbsolutePath + "/" + WarningsFile)
    val xml = toXML(warnings)
    val out = new BufferedWriter(new FileWriter(file))
    out.write(xml.toString())
    out.close()
  }

  def toXML(warnings: Seq[Warning]) = {
    <scapegoat count={warnings.size.toString}>
      {warnings.map(warning2xml)}
    </scapegoat>
  }

  private def warning2xml(warning: Warning) = {
    <warning line={warning.line.toString}>
      {warning.text.trim}
    </warning>
  }
}
