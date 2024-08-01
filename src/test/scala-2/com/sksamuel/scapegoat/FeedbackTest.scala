package com.sksamuel.scapegoat

import scala.reflect.internal.util.NoPosition
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.StoreReporter

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{OneInstancePerTest, PrivateMethodTester}

/** @author Stephen Samuel */
class FeedbackTest extends AnyFreeSpec with Matchers with OneInstancePerTest with PrivateMethodTester {

  val position: NoPosition.type = NoPosition
  val defaultSourcePrefix = "src/main/scala/"

  class DummyInspection(
    text: String,
    defaultLevel: Level,
    description: String,
    explanation: String
  ) extends Inspection(text, defaultLevel, description, explanation) {
    override def inspector(ctx: InspectionContext): Inspector = ???
  }

  "FeedbackScala2" - {
    "should report to reporter" - {
      "for error" in {
        val inspection = new DummyInspection(
          "My default is Error",
          Levels.Error,
          "This is description.",
          "This is explanation."
        )
        val reporter = new StoreReporter(new Settings())
        val feedback =
          new FeedbackScala2(reporter, testConfiguration(consoleOutput = true, defaultSourcePrefix))
        feedback.warn(position, inspection)
        reporter.infos should have size 1
        val info = reporter.infos.head
        info.pos === position
        info.msg === "[scapegoat] [DummyInspection] My default is Error\n  This is explanation."
        info.severity === reporter.ERROR
      }

      "for warning" in {
        val inspection = new DummyInspection(
          "My default is Warning",
          Levels.Warning,
          "This is description.",
          "This is explanation."
        )
        val reporter = new StoreReporter(new Settings())
        val feedback =
          new FeedbackScala2(reporter, testConfiguration(consoleOutput = true, defaultSourcePrefix))
        feedback.warn(position, inspection)
        reporter.infos should have size 1
        val info = reporter.infos.head
        info.pos === position
        info.msg === "[scapegoat] [DummyInspection] My default is Warning\n  This is explanation."
        info.severity === reporter.WARNING
      }

      "for info" in {
        val inspection = new DummyInspection(
          "My default is Info",
          Levels.Info,
          "This is description.",
          "This is explanation."
        )
        val reporter = new StoreReporter(new Settings())
        val feedback =
          new FeedbackScala2(reporter, testConfiguration(consoleOutput = true, defaultSourcePrefix))
        feedback.warn(position, inspection)
        reporter.infos should have size 1
        val info = reporter.infos.head
        info.pos === position
        info.msg === "[scapegoat] [DummyInspection] My default is Info\n  This is explanation."
        info.severity === reporter.INFO
      }
    }

    "should use proper paths" - {
      "for `src/main/scala`" in {
        val reporter = new StoreReporter(new Settings())
        val feedback =
          new FeedbackScala2(reporter, testConfiguration(consoleOutput = true, defaultSourcePrefix))
        val source = "src/main/scala/com/sksamuel/scapegoat/Test.scala"
        val result = feedback.normalizeSourceFile(source)
        result should ===("com.sksamuel.scapegoat.Test.scala")
      }

      "for `app`" in {
        val reporter = new StoreReporter(new Settings())
        val feedback = new FeedbackScala2(reporter, testConfiguration(consoleOutput = true, "app/"))
        val source = "app/com/sksamuel/scapegoat/Test.scala"
        val result = feedback.normalizeSourceFile(source)
        result should ===("com.sksamuel.scapegoat.Test.scala")
      }

      "should add trailing / to the sourcePrefix automatically" in {
        val reporter = new StoreReporter(new Settings())
        val feedback = new FeedbackScala2(reporter, testConfiguration(consoleOutput = true, "app/custom"))
        val source = "app/custom/com/sksamuel/scapegoat/Test.scala"
        val result = feedback.normalizeSourceFile(source)
        result should ===("com.sksamuel.scapegoat.Test.scala")
      }
    }

    "should use minimal warning level in reports" - {
      "for `info`" in {
        val inspectionError = new DummyInspection(
          "My default is Error",
          Levels.Error,
          "This is description.",
          "This is explanation."
        )
        val inspectionWarning = new DummyInspection(
          "My default is Warning",
          Levels.Warning,
          "This is description.",
          "This is explanation."
        )
        val inspectionInfo = new DummyInspection(
          "My default is Info",
          Levels.Info,
          "This is description.",
          "This is explanation."
        )
        val inspectionIgnored = new DummyInspection(
          "My default is Ignore",
          Levels.Ignore,
          "This is description.",
          "This is explanation."
        )
        val inspections = Seq(inspectionError, inspectionWarning, inspectionInfo, inspectionIgnored)
        val reporter = new StoreReporter(new Settings())
        val feedback =
          new FeedbackScala2(
            reporter,
            testConfiguration(consoleOutput = true, defaultSourcePrefix, Levels.Info)
          )
        inspections.foreach(inspection => feedback.warn(position, inspection))
        feedback.warningsWithMinimalLevel.length should be(3)
      }

      "for `warning`" in {
        val inspectionError = new DummyInspection(
          "My default is Error",
          Levels.Error,
          "This is description.",
          "This is explanation."
        )
        val inspectionWarning = new DummyInspection(
          "My default is Warning",
          Levels.Warning,
          "This is description.",
          "This is explanation."
        )
        val inspectionInfo = new DummyInspection(
          "My default is Info",
          Levels.Info,
          "This is description.",
          "This is explanation."
        )
        val inspectionIgnored = new DummyInspection(
          "My default is Ignore",
          Levels.Ignore,
          "This is description.",
          "This is explanation."
        )
        val inspections = Seq(inspectionError, inspectionWarning, inspectionInfo, inspectionIgnored)
        val reporter = new StoreReporter(new Settings())
        val feedback = new FeedbackScala2(
          reporter,
          testConfiguration(consoleOutput = false, defaultSourcePrefix, Levels.Warning)
        )
        inspections.foreach(inspection => feedback.warn(position, inspection))
        feedback.warningsWithMinimalLevel.length should be(2)
        feedback.warningsWithMinimalLevel
          .map(_.level) should contain only (Seq(Levels.Warning, Levels.Error): _*)
      }

      "for `error`" in {
        val inspectionError = new DummyInspection(
          "My default is Error",
          Levels.Error,
          "This is description.",
          "This is explanation."
        )
        val inspectionWarning = new DummyInspection(
          "My default is Warning",
          Levels.Warning,
          "This is description.",
          "This is explanation."
        )
        val inspectionInfo = new DummyInspection(
          "My default is Info",
          Levels.Info,
          "This is description.",
          "This is explanation."
        )
        val inspectionIgnored = new DummyInspection(
          "My default is Ignore",
          Levels.Ignore,
          "This is description.",
          "This is explanation."
        )
        val inspections = Seq(inspectionError, inspectionWarning, inspectionInfo, inspectionIgnored)
        val reporter = new StoreReporter(new Settings())
        val feedback =
          new FeedbackScala2(
            reporter,
            testConfiguration(consoleOutput = false, defaultSourcePrefix, Levels.Error)
          )
        inspections.foreach(inspection => feedback.warn(position, inspection))
        feedback.warningsWithMinimalLevel.length should be(1)
        feedback.warningsWithMinimalLevel.map(_.level) should contain only (Seq(Levels.Error): _*)
      }
    }
  }

  private def testConfiguration(
    consoleOutput: Boolean,
    sourcePrefix: String,
    minimalLevel: Level = Levels.Info
  ) = TestConfiguration.configuration.copy(
    consoleOutput = consoleOutput,
    sourcePrefix = sourcePrefix,
    minimalLevel = minimalLevel
  )
}
