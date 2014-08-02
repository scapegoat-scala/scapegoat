package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.string.IncorrectNumberOfArgsToFormat
import org.scalatest.{OneInstancePerTest, FreeSpec, Matchers}

/** @author Stephen Samuel */
class IncorrectNumberOfArgsToFormatTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new IncorrectNumberOfArgsToFormat)

  "incorrect number of arguments to format" - {
    "should report warning" in {

      val code = """object Test {
                      "%s %.2f".format("sam")
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "correct number of arguments to format" - {
    "should not report warning" in {

      val code = """object Test {
                      "%s %.2f".format("sam", 4.5)
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
