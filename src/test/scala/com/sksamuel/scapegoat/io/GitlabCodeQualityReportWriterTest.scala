package com.sksamuel.scapegoat.io

import java.security.MessageDigest

import com.sksamuel.scapegoat.{Levels, Warning}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class GitlabCodeQualityReportWriterTest extends AnyFreeSpec with Matchers {

  "GitlabCodeQualityReportWriter" - {

    "should transform feedback" in {
      val warnings = Seq(
        Warning(
          "Use of Option.get",
          13,
          Levels.Error,
          "/home/johnnei/git/scapegoat/src/main/scala/com/sksamuel/File.scala",
          "com.sksamuel.File.scala",
          Some("File.this.d.get"),
          "Using Option.get defeats the purpose",
          "com.sksamuel.scapegoat.inspections.option.OptionGet"
        ),
        Warning(
          "List.size is O(n)",
          13,
          Levels.Info,
          "/home/johnnei/git/scapegoat/src/main/scala/com/sksamuel/File.scala",
          "com.sksamuel.File.scala",
          None,
          "List.size is O(n). Consider using...",
          "com.sksamuel.scapegoat.inspections.collections.ListSize"
        )
      )

      val report = GitlabCodeQualityReportWriter
        .toCodeQualityElements(
          warnings,
          Some("/home/johnnei/git/scapegoat"),
          MessageDigest.getInstance("MD5")
        )
      report should be(
        Seq(
          CodeQualityReportElement(
            // Extra dot after warning text to improve readability
            "Use of Option.get. Using Option.get defeats the purpose",
            "com.sksamuel.scapegoat.inspections.option.OptionGet",
            CriticalSeverity,
            Location("src/main/scala/com/sksamuel/File.scala", Lines(13)),
            "909b14c15a3a3891659251f133058264"
          ),
          CodeQualityReportElement(
            // Warning text is trimmed to avoid duplicate text
            "List.size is O(n). Consider using...",
            "com.sksamuel.scapegoat.inspections.collections.ListSize",
            InfoSeverity,
            Location("src/main/scala/com/sksamuel/File.scala", Lines(13)),
            "f79bc3223909939407272a1db37a6d17"
          )
        )
      )
    }
  }

}
