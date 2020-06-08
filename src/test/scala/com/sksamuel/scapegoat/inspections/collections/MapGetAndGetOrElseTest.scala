package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest
class MapGetAndGetOrElseTest extends InspectionTest {

  override val inspections = Seq(new MapGetAndGetOrElse)

  private def getOrElseAssertion(code: String): Unit = {
    compileCodeSnippet(code)
    compiler.scapegoat.feedback.warnings.size shouldBe 1
    compiler.scapegoat.feedback.warnings.head.text shouldBe "Use of Map.get().getOrElse instead of Map.getOrElse"
  }

  "Map with get followed by getOrElse" - {
    "should report a warning" - {
      "when used with the default scala.Map" in {
        val code = """class Test {
                     | val numMap = Map(1 -> "one", 2 -> "two")
                     | numMap.get(1).getOrElse("unknown")
                     } """.stripMargin

        getOrElseAssertion(code)
      }

      "when used with a mutable Map" in {
        val code = """class Test {
                     | val numMap = scala.collection.mutable.Map("one" -> 1, "two" -> 2)
                     | numMap.get("one").getOrElse(-1)
             } """.stripMargin

        getOrElseAssertion(code)
      }

      "when used with a Map definition" in {
        val code = """class Test {
                     | Map("John" -> "Smith", "Peter" -> "Rabbit").get("Sarah").getOrElse("-")
             } """.stripMargin

        getOrElseAssertion(code)
      }
    }
  }
}
