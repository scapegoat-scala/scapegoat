package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class PreferSeqEmptyTest extends InspectionTest {

  override val inspections = Seq(new PreferSeqEmpty)

  "should report warning" - {
    "with empty seq apply" in {

      val code = """object Test {
                      val a = Seq[String]()
                      val b = Set.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "should not report warning" - {
    "with non empty seq apply" in {
      val code = """object Test {
                      val a = Seq("sam")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "with Seq.empty" in {
      val code = """object Test {
                      val b = Seq.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "with mutable.Seq" in {
      val code = """object Test {
                      val b = scala.collection.mutable.Seq()
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

  }
}
