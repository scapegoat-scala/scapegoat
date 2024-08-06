package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class ParameterlessMethodReturnsUnitTest extends InspectionTest {

  override val inspections = Seq[Inspection](new ParameterlessMethodReturnsUnit)

  "ParameterlessMethodReturnsUnit" - {
    "should report warning" - {
      "for methods with unit and no params" in {

        val code =
          """object Test {
               def paramless: Unit = ()
             } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for methods with unit and params" in {

        val code =
          """object Test {
               def params(): Unit = ()
             } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for parameterless non unit methods" in {

        val code =
          """object Test {
               def params: Int = 3
             } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
