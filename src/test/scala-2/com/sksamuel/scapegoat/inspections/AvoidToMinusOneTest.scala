package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class AvoidToMinusOneTest extends InspectionTest {

  override val inspections = Seq[Inspection](new AvoidToMinusOne)

  "AvoidToMinusOne" - {
    "should report warning" - {
      "for for-loop with n-1" in {

        val code = """class Test {
                        val k = 10
                        for (n <- 1 to k - 1) {
                          println("sam")
                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      " for-loop with n-2" in {

        val code = """class Test {
                     |                        val k = 10
                     |                        for (n <- 1 to k - 2) {
                     |                          println("sam")
                     |                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
