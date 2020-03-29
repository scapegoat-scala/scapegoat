package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class SimplifyBooleanExpressionTest extends InspectionTest {

  override val inspections = Seq(new SimplifyBooleanExpression)

  "incorrectly named exceptions" - {
    "should report warning" in {

      val code = """object Test {
                      val b = false
                      val f = b == false
                    }
                    """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
