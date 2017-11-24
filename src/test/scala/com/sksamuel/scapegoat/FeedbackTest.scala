package com.sksamuel.scapegoat

import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

import scala.reflect.internal.util.NoPosition
import scala.tools.nsc.reporters.StoreReporter

/** @author Stephen Samuel */
class FeedbackTest
    extends FreeSpec
    with Matchers
    with OneInstancePerTest {

  val position = NoPosition

  class DummyInspection(text: String, defaultLevel: Level) extends Inspection(text, defaultLevel) {
    override def inspector(context: InspectionContext): Inspector = ???
  }

  "Feedback" - {
    "should report to reporter" - {
      "for error" in {
        val inspection = new DummyInspection("My default is Error", Levels.Error)
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter)
        feedback.warn(position, inspection)
        reporter.infos should contain(reporter.Info(position, "My default is Error", reporter.ERROR))
      }
      "for warning" in {
        val inspection = new DummyInspection("My default is Warning", Levels.Warning)
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter)
        feedback.warn(position, inspection)
        reporter.infos should contain(reporter.Info(position, "My default is Warning", reporter.WARNING))
      }
      "for info" in {
        val inspection = new DummyInspection("My default is Info", Levels.Info)
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter)
        feedback.warn(position, inspection)
        reporter.infos should contain(reporter.Info(position, "My default is Info", reporter.INFO))
      }
    }
  }
}