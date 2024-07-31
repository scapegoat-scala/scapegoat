package com.sksamuel.scapegoat

import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

abstract class InspectionTest(inspection: Class[? <: Inspection])
    extends AnyFreeSpec
    with OneInstancePerTest
    with Matchers:

  extension (ws: Seq[Warning])
    /**
     * Erases (inspection level) constant fields or temporary file based fields.
     * @return
     *   Normalized Seq of Warnings that focus on being assertable reliably
     */
    def assertable: Seq[Warning] = ws.map { w =>
      w.copy(
        text = "text",
        sourceFileFull = "sourceFileFull",
        sourceFileNormalized = "sourceFileNormalized",
        explanation = "explanation"
      )
    }

  def warning(line: Int, level: Level, snippet: Option[String]): Warning = Warning(
    text = "text",
    line = line,
    level = level,
    sourceFileFull = "sourceFileFull",
    sourceFileNormalized = "sourceFileNormalized",
    snippet = snippet,
    explanation = "explanation",
    inspection = inspection.getCanonicalName
  )

  val runner = new DottyRunner(inspection)
