package com.sksamuel.scapegoat

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class AllInspectionsTest extends AnyFreeSpec with Matchers {

  val code = "\\s[A-Za-z0-9.]+\\(\\)\\s".r

  ScapegoatConfig.inspections.foreach { insp =>
    insp.getClass.getSimpleName - {
      "should have a properly-formatted description" in {
        insp.description.trim shouldNot be("")
        insp.description.last should be('.')
        insp.description.head.toUpper should be(insp.description.head)
        code.findAllIn(insp.description).hasNext should be(false)
      }

      "should have a properly-formatted explanation" in {
        insp.explanation.trim shouldNot be("")
        insp.explanation.last should (be('.') or be('?'))
        insp.explanation.head.toUpper should be(insp.explanation.head)
        code.findAllIn(insp.explanation).hasNext should be(false)
      }
    }
  }
}
