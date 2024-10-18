package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.InspectionTest

class AvoidRequireTest extends InspectionTest(classOf[AvoidRequire]) {

  "require use" - {
    "should return warning in method" in {
      val code =
        """
          object Test {
            def test(x: Int): Int = {
              require(x == 1)
              x
            }
          }
          """.stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.warnings.assertable.size shouldBe 1
    }

    "should return warning in class" in {
      val code =
        """
          class Test(val x: Int) {
            require(x == 1, "oops")
          }
          """.stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.warnings.assertable.size shouldBe 1
    }

    "should not return warnin on own require method" in {
      val code =
        """
          object T {
              def require(x: Boolean): Boolean = false

              def foo(): Boolean = {
                require(1 == 1)
              }
          }
          """.stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.warnings.assertable.size shouldBe 0
    }

    "should not return warning if no require" in {
      val code =
        """
          class Test(val x: Int) { }
          """.stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.warnings.assertable.size shouldBe 0
    }
  }

}
