package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class ComparisonWithSelfInspectionTest extends InspectionTest {

  override val inspections = Seq(new ComparisonWithSelf)

  "ComparisonWithSelf" - {
    "should report warning" in {

      val code = """object Test {
                      val b = true
                      if (b == b) {
                      } else if(b != b) {
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
