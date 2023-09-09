package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class SimplifyBooleanExpressionTest extends InspectionTest {

  override val inspections = Seq[Inspection](new SimplifyBooleanExpression)

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
