package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class DoubleNegationTest extends InspectionTest {

  override val inspections = Seq[Inspection](new DoubleNegation)

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
