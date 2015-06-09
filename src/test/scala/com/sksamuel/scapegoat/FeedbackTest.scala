package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections.AnyUse
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

import scala.reflect.internal.util.NoPosition
import scala.tools.nsc.reporters.StoreReporter

/** @author Stephen Samuel */
class FeedbackTest
    extends FreeSpec
    with Matchers
    with OneInstancePerTest {

  val message = "Some problem"
  val position = NoPosition
  val inspection = new AnyUse

  "Feedback" - {
    "should report to reporter" - {
      "for error" in {
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter)
        feedback.warn(message, position, Levels.Error, inspection)
        reporter.infos should contain(reporter.Info(position, message, reporter.ERROR))
      }
      "for warning" in {
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter)
        feedback.warn(message, position, Levels.Warning, inspection)
        reporter.infos should contain(reporter.Info(position, message, reporter.WARNING))
      }
      "for info" in {
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter)
        feedback.warn(message, position, Levels.Info, inspection)
        reporter.infos should contain(reporter.Info(position, message, reporter.INFO))
      }
    }
  }
}