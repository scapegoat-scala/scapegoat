package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class SubstringZeroTest extends InspectionTest {

  override val inspections = Seq(new SubstringZero)

  "String.substring(0)" - {
    "should report warning" in {

      val code = """object Test {
                      val name = "sam"
                      name.substring(0)
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "String.substring(function)" - {
    "should not report warning" in {

      val code = """object Test {
                     val name = "sam"
                     def index = 0
                     name.substring(index)
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
