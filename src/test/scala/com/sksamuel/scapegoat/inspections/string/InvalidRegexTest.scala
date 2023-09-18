package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class InvalidRegexTest extends InspectionTest {

  override val inspections = Seq[Inspection](new InvalidRegex)

  "invalid regex" - {
    "should report warning" in {

      val code = """object Test {
                      val regex = "?".r
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
