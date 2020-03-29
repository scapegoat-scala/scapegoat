package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class DoubleNegationTest extends InspectionTest {

  override val inspections = Seq(new DoubleNegation)

  "DoubleNegation" - {
    "should report warning" in {

      val code = """object Test {
                      val b = true
                      val c = !(!b)
                    }
                 """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
