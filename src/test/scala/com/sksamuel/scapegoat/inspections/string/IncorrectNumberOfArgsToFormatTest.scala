package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class IncorrectNumberOfArgsToFormatTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new IncorrectNumberOfArgsToFormat)

  "IncorrectNumberOfArgsToFormat" - {
    "should report warning" in {

      val code = """object Test {
                      "%s %.2f".format("sam")
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
    "should not report warning" - {
      "for correct number of args" in {
        val code = """object Test {    "%s %.2f".format("sam", 4.5)  } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for correct number of args with array" in {
        val code = """object Test {  val arr = Array("sam");  "this is my [%s] string" format arr } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
