package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

class PreferMapEmptyTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new PreferMapEmpty)

  "map apply" - {
    "should report warning" in {

      val code = """object Test {
                      val a = Map[String, String]()
                      val b = Map.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "non empty map apply" - {
    "should not report warning" in {

      val code = """object Test {
                      val a = Map(1 -> 2)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
  "Map.empty" - {
    "should not report warning" in {

      val code = """object Test {
                      val b = Map.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
