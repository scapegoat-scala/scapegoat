package com.sksamuel.scapegoat.io

import java.io.{BufferedWriter, File, FileWriter}

import com.sksamuel.scapegoat.Feedback

trait ReportWriter {

  protected def fileName: String

  protected def generate(feedback: Feedback): String

  private def serialize(file: File, str: String): Unit = {
    val out = new BufferedWriter(new FileWriter(file))
    out.write(str)
    out.close()
  }

  def write(targetDir: File, feedback: Feedback): File = {
    targetDir.mkdirs()
    val file = new File(s"${targetDir.getAbsolutePath}/$fileName")
    serialize(file, generate(feedback))
    file
  }
}
