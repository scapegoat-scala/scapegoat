package com.sksamuel.scapegoat

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class AllInspectionsTest extends AnyFreeSpec with Matchers {

  ScapegoatConfig.inspections.foreach { insp =>
    insp.getClass.getSimpleName - {
      "should have a properly-formatted description" in {
        insp.description.trim shouldNot be("")
        insp.description.last should be('.')
        insp.description.head.toUpper should be(insp.description.head)
      }

      "should have a properly-formatted explanation" in {
        insp.explanation.trim shouldNot be("")
        insp.explanation.last should (be('.') or be('?'))
        insp.explanation.head.toUpper should be(insp.explanation.head)
      }
    }
  }
}
