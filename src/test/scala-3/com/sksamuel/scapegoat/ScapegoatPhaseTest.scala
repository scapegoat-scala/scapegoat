package com.sksamuel.scapegoat

import java.io.{File, FilenameFilter}

import com.sksamuel.scapegoat.inspections.option.OptionGet
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

class ScapegoatPhaseTest extends AnyFreeSpec with should.Matchers {

  private def runDotty(report: String, disabledAll: Boolean = false): File = {
    val targetFolder = DottyRunner.createTempDataDir()
    val dotty = new DottyRunner(
      classOf[OptionGet],
      report,
      if (disabledAll) List("all") else List("none"),
      () => targetFolder
    )
    val _ = dotty.compileCodeSnippet("class Test {}")
    targetFolder
  }

  private val reportFilter = new FilenameFilter {
    private val reportExtensions = "html" :: "xml" :: "md" :: "json" :: Nil
    override def accept(dir: File, name: String): Boolean = reportExtensions.exists(name.endsWith)
  }

  "ScapegoatPhase" - {

    "be disablable" in {
      val outputDir = runDotty("html", true)
      outputDir.listFiles(reportFilter) should contain theSameElementsAs (Array.empty[File])
    }

    "generate reports" - {
      "should generate html report" in {
        val outputDir = runDotty("html")
        outputDir.listFiles(reportFilter) should contain theSameElementsAs (Array(
          new File(outputDir, "scapegoat.html")
        ))
      }

      "should generate xml report" in {
        val outputDir = runDotty("xml")
        outputDir.listFiles(reportFilter) should contain theSameElementsAs (Array(
          new File(outputDir, "scapegoat.xml")
        ))
      }

      "should generate scalastyle report" in {
        val outputDir = runDotty("scalastyle")
        outputDir.listFiles(reportFilter) should contain theSameElementsAs (Array(
          new File(outputDir, "scapegoat-scalastyle.xml")
        ))
      }

      "should generate markdown report" in {
        val outputDir = runDotty("markdown")
        outputDir.listFiles(reportFilter) should contain theSameElementsAs (Array(
          new File(outputDir, "scapegoat.md")
        ))
      }

      "should generate gitlab code quality report" in {
        val outputDir = runDotty("gitlab-codequality")
        outputDir.listFiles(reportFilter) should contain theSameElementsAs (Array(
          new File(outputDir, "scapegoat-gitlab.json")
        ))
      }
    }
  }
}
