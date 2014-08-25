package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class PreferSetEmptyTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

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
}
