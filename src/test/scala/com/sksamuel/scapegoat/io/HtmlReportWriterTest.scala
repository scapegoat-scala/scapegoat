package com.sksamuel.scapegoat.io

import com.sksamuel.scapegoat.{Levels, Warning}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class HtmlReportWriterTest extends AnyFreeSpec with Matchers {

  "HtmlReportWriter" - {
    "should escape angle brackets" in {
      val warnings = Seq(
        Warning(
          "Pointless type bounds",
          13,
          Levels.Error,
          "/home/johnnei/git/scapegoat/src/main/scala/com/sksamuel/File.scala",
          "com.sksamuel.File.scala",
          None,
          explanation = "Finds type bounds of the form `A <: Any` or `A >: Nothing`.",
          inspection = "com.sksamuel.scapegoat.inspections.inference.PointlessTypeBounds"
        )
      )

      val escapedReport = Seq(
        <div class="warning">
          <div class="source">
            com.sksamuel.File.scala:13
          </div>
          <div class="title">
            <span class="label label-danger">Error</span>&nbsp;<span>Pointless type bounds</span>&nbsp; <span class="inspection">
            com.sksamuel.scapegoat.inspections.inference.PointlessTypeBounds
          </span>
          </div>
          <div>
            <span>Finds type bounds of the form <code>A &lt;: Any</code> or <code>A &gt;: Nothing</code>.</span>
          </div>
        </div>
      )

      val report = HtmlReportWriter.warnings(warnings)
      report.toString() shouldEqual escapedReport.toString()
    }
  }
}
