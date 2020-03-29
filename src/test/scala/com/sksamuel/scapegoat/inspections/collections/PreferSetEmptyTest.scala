package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class PreferSetEmptyTest extends InspectionTest {

  override val inspections = Seq(new PreferSetEmpty)

  "set apply" - {
    "should report warning" in {

      val code = """object Test {
                      val a = Set[String]()
                      val b = Set.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "non empty set apply" - {
    "should not report warning" in {

      val code = """object Test {
                      val a = Set("sam")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
  "Set.empty" - {
    "should not report warning" in {

      val code = """object Test {
                      val b = Set.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
  "mutable.Set" - {
    "should not report a warning" in {
      val code = """
                   | import scala.collection.mutable
                   | object Test {
                   |  val set = mutable.Set[String]()
                   |  set.add("a")
                   | }""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0

    }
  }
}
