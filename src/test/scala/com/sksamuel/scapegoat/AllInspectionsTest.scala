package com.sksamuel.scapegoat

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class AllInspectionsTest extends AnyFreeSpec with Matchers {

  ScapegoatConfig.inspections.foreach { insp =>
    insp.getClass.getSimpleName - {
      "should have a description" in {
        insp.description.trim shouldNot be("")
      }

      "its description should end in ." in {
        insp.description.last should be('.')
      }
    }
  }
}
