package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

class AvoidRequireTest extends InspectionTest {
  override val inspections = Seq[Inspection](new AvoidRequire)

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

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }

    "should return warning in class" in {
      val code =
        """
          class Test(val x: Int) {
            require(x == 1, "oops")
          }
          """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }

    "should not return warning on own require method" in {
      val code =
        """
          object T {
              def require(x: Boolean): Boolean = false

              def foo(): Boolean = {
                require(1 == 1)
              }
          }
          """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should not return warning if no require" in {
      val code =
        """
          class Test(val x: Int) { }
          """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
