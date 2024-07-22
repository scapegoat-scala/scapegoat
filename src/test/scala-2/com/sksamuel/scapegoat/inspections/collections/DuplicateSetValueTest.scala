package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class DuplicateSetValueTest extends InspectionTest {

  override val inspections = Seq[Inspection](new DuplicateSetValue)

  "duplicate set literals" - {
    "should report warning" in {
      val code = """object Test {
                      Set("sam", "aylesbury", "sam")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "non duplicate set literals" - {
    "should not report warning" in {
      val code = """object Test {
                      Set("name", "location", "aylesbury", "bob")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }

  "duplicate etas" - {
    "should not report warning" in {
      val code = """object Test {
                      def name = "could be random"
                      Set(name, "middle", name)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
