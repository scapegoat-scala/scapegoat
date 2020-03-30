package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class StripMarginOnRegexTest extends InspectionTest {

  override val inspections = Seq(new StripMarginOnRegex)

  "StripMarginOnRegex" - {
    "should report warning" - {
      "for regex containing | that calls strip margin before r" in {
        val code = """object Test {
                        val regex = "match|this".stripMargin.r
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for regex without | that calls strip margin before r" in {
        val code = """object Test {
                        val regex = "match_this".stripMargin.r
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for plain regex" in {
        val code = """object Test {
                        val regex = "match|this".r
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
