package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest
class PreferMapEmptyTest extends InspectionTest {

  override val inspections = Seq(new PreferMapEmpty)

  "should report warning" - {
    "with map apply" in {

      val code = """object Test {
                      val a = Map[String, String]()
                      val b = Map.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "should not report warning" - {
    "with non empty Map apply" in {
      val code = """object Test {
                      val a = Map(1 -> 2)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "with Map.empty" in {
      val code = """object Test {
                      val b = Map.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "with mutable.Map" in {
      val code = """import scala.collection.mutable.Map
                    object Test {
                      val b = Map()
                    }""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
