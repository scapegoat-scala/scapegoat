package com.sksamuel.scapegoat.inspections.traits

import com.sksamuel.scapegoat.InspectionTest

class AbstractTraitTest extends InspectionTest(classOf[AbstractTrait]) {

  "abstract trait use" - {
    "should report warning" in {
      val code = "abstract trait Test { }"

      val feedback = runner.compileCodeSnippet(code)
      feedback.warnings.assertable.size shouldBe 1
    }

    "should not report warning on sealed" in {
      val code = "sealed trait Test { val x: Int = 1 }"

      val feedback = runner.compileCodeSnippet(code)
      feedback.warnings.assertable.size shouldBe 0
    }

    "should not report warning on trait without modifiers" in {
      val code = "trait Test1 { val x: Int = 1 }"

      val feedback = runner.compileCodeSnippet(code)
      feedback.warnings.assertable.size shouldBe 0
    }

    "should not report on private trait" in {
      val code = "private trait Test1 { val x: Int = 1 }"

      val feedback = runner.compileCodeSnippet(code)
      feedback.warnings.assertable.size shouldBe 0
    }
  }
}
