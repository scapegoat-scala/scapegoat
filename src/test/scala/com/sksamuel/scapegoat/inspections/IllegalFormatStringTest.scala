package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class IllegalFormatStringTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new IllegalFormatString)

  "unknown conversion type" - {
    "should report warning" in {

      val code = """object Test {
                      "%5.5q".format("sam")
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "space after %" - {
    "should report warning" in {

      val code = """object Test {
                      "% s".format("sam")
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "unknown flag in format" - {
    "should report warning" in {

      val code = """object Test {
                      "%qs".format("sam")
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "illegal format width" - {
    "should report warning" in {

      val code = """object Test {
                      "%.-5s".format("sam")
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "missing format width" - {
    "should report warning" in {

      val code = """object Test {
                      "%.s".format("sam")
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
