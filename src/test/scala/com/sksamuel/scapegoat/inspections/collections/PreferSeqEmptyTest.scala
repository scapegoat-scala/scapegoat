package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class PreferSeqEmptyTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new PreferSeqEmpty)

  "empty seq apply" - {
    "should report warning" in {

      val code = """object Test {
                      val a = Seq[String]()
                      val b = Set.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "non empty seq apply" - {
    "should not report warning" in {

      val code = """object Test {
                      val a = Seq("sam")
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
