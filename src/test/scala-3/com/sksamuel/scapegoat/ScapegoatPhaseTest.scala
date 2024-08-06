package com.sksamuel.scapegoat

import java.io.File
import java.nio.file.Files

import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Contexts.ContextBase
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

class ScapegoatPhaseTest extends AnyFreeSpec with should.Matchers {

  private def configuration(reports: Reports): (File, Configuration) = {
    val tmpDir = Files.createTempDirectory("scapegoat").toFile
    tmpDir.deleteOnExit()
    val configuration = Configuration(
      dataDir = Some(tmpDir),
      disabledInspections = Nil,
      enabledInspections = Nil,
      ignoredFiles = Nil,
      consoleOutput = false,
      verbose = true,
      reports = reports,
      customInspectors = Nil,
      sourcePrefix = "",
      minimalLevel = Levels.Info,
      overrideLevels = Map.empty
    )
    (tmpDir, configuration)
  }

  private val noReports = Reports(
    disableXML = true,
    disableHTML = true,
    disableScalastyleXML = true,
    disableMarkdown = true,
    disableGitlabCodeQuality = true
  )

  "ScapegoatPhase" - {

    "be disablable" in {
      val (outputDir, config) = configuration(reports = noReports.copy(disableHTML = false))
      val phase = new ScapegoatPhase(config.copy(disabledInspections = List("all")), Nil)
      implicit val ctx: Context = (new ContextBase).initialCtx

      val _ = phase.runOn(Nil)

      assert(outputDir.listFiles().size === 0)
    }

    "generate reports" - {
      "should generate html report" in {
        val (outputDir, config) = configuration(reports = noReports.copy(disableHTML = false))
        val phase = new ScapegoatPhase(config, Nil)
        implicit val ctx: Context = (new ContextBase).initialCtx

        val _ = phase.runOn(Nil)

        assert(new File(outputDir, "scapegoat.html").exists() === true)
      }
      "should generate xml report" in {
        val (outputDir, config) = configuration(reports = noReports.copy(disableXML = false))
        val phase = new ScapegoatPhase(config, Nil)
        implicit val ctx: Context = (new ContextBase).initialCtx

        val _ = phase.runOn(Nil)

        assert(new File(outputDir, "scapegoat.xml").exists() === true)
      }
      "should generate scalastyle report" in {
        val (outputDir, config) = configuration(reports = noReports.copy(disableScalastyleXML = false))
        val phase = new ScapegoatPhase(config, Nil)
        implicit val ctx: Context = (new ContextBase).initialCtx

        val _ = phase.runOn(Nil)

        assert(new File(outputDir, "scapegoat-scalastyle.xml").exists() === true)
      }
      "should generate markdown report" in {
        val (outputDir, config) = configuration(reports = noReports.copy(disableMarkdown = false))
        val phase = new ScapegoatPhase(config, Nil)
        implicit val ctx: Context = (new ContextBase).initialCtx

        val _ = phase.runOn(Nil)

        assert(new File(outputDir, "scapegoat.md").exists() === true)
      }
      "should generate gitlab code quality report" in {
        val (outputDir, config) = configuration(reports = noReports.copy(disableGitlabCodeQuality = false))
        val phase = new ScapegoatPhase(config, Nil)
        implicit val ctx: Context = (new ContextBase).initialCtx

        val _ = phase.runOn(Nil)

        assert(new File(outputDir, "scapegoat-gitlab.json").exists() === true)
      }
    }
  }
}
