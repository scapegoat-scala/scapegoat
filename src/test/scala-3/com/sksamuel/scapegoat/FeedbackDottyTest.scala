package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections.option.OptionGet
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Contexts.ContextBase
import dotty.tools.dotc.interfaces.Diagnostic
import dotty.tools.dotc.reporting.StoreReporter
import dotty.tools.dotc.util.NoSourcePosition
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

class FeedbackDottyTest extends AnyFreeSpec with should.Matchers {

  private val configuration = Configuration(
    dataDir = None,
    disabledInspections = Nil,
    enabledInspections = Nil,
    ignoredFiles = Nil,
    consoleOutput = true,
    verbose = true,
    reports = Reports(false, false, false, false, false),
    customInspectors = Nil,
    sourcePrefix = "",
    minimalLevel = Levels.Info,
    overrideLevels = Map.empty
  )

  "FeedbackDotty" - {
    "override inspection levels" - {
      "all takes priority" in {
        implicit val ctx: Context = (new ContextBase).initialCtx
        val feedback = FeedbackDotty(
          configuration.copy(overrideLevels =
            Map(
              "all" -> Levels.Info,
              "OptionGet" -> Levels.Warning
            )
          )
        )
        feedback.warn(NoSourcePosition, new OptionGet())
        feedback.warnings(Levels.Info).size.shouldBe(1)
      }
      "able to downgrade errors" in {
        implicit val ctx: Context = (new ContextBase).initialCtx
        val feedback = FeedbackDotty(
          configuration.copy(overrideLevels =
            Map(
              "OptionGet" -> Levels.Warning
            )
          )
        )
        feedback.warn(NoSourcePosition, new OptionGet())
        feedback.warnings(Levels.Warning).size.shouldBe(1)
      }
    }

    "report to compiler" - {
      "error" in {
        val reporter = new StoreReporter()
        implicit val ctx: Context = (new ContextBase).initialCtx.fresh.setReporter(reporter)
        val feedback = FeedbackDotty(configuration)
        val inspection = new OptionGet()
        feedback.warn(NoSourcePosition, inspection)
        reporter.pendingMessages.headOption.map(_.level).shouldBe(Some(Diagnostic.ERROR))
      }
      "warning" in {
        val reporter = new StoreReporter()
        implicit val ctx: Context = (new ContextBase).initialCtx.fresh.setReporter(reporter)
        val feedback = FeedbackDotty(configuration.copy(overrideLevels = Map("OptionGet" -> Levels.Warning)))
        val inspection = new OptionGet()
        feedback.warn(NoSourcePosition, inspection)
        reporter.pendingMessages.headOption.map(_.level).shouldBe(Some(Diagnostic.WARNING))
      }
      "info" in {
        val reporter = new StoreReporter()
        implicit val ctx: Context = (new ContextBase).initialCtx.fresh.setReporter(reporter)
        val feedback = FeedbackDotty(configuration.copy(overrideLevels = Map("OptionGet" -> Levels.Info)))
        val inspection = new OptionGet()
        feedback.warn(NoSourcePosition, inspection)
        reporter.pendingMessages.headOption.map(_.level).shouldBe(Some(Diagnostic.INFO))
      }
      "ignore" in {
        val reporter = new StoreReporter()
        implicit val ctx: Context = (new ContextBase).initialCtx.fresh.setReporter(reporter)
        val feedback = FeedbackDotty(configuration.copy(overrideLevels = Map("OptionGet" -> Levels.Ignore)))
        val inspection = new OptionGet()
        feedback.warn(NoSourcePosition, inspection)
        reporter.pendingMessages.isEmpty.shouldBe(true)
      }
    }
  }
}
