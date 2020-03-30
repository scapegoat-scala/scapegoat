package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{isScala213, InspectionTest}

/** @author Stephen Samuel */
class EmptyInterpolatedStringTest extends InspectionTest {

  override val inspections = Seq(new EmptyInterpolatedString)

  "EmptyInterpolatedString" - {
    "should report warning (for Scala < 2.13)" in {

      val code = """object Test {
                      val name = "sam"
                      s"hello name"
                   } """.stripMargin
      val expectedWarnings = if (isScala213) 0 else 1

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings should have size expectedWarnings
    }
  }
  "non empty interpolated string" - {
    "should not report warning" in {

      val code = """object Test {
                     val name = "sam"
                      s"hello $name"
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
