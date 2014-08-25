package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class SubstringZeroTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new SubstringZero)

  "String.substring(0)" - {
    "should report warning" in {

      val code = """object Test {
                      val name = "sam"
                      name.substring(0)
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "String.substring(function)" - {
    "should not report warning" in {

      val code = """object Test {
                     val name = "sam"
                     def index = 0
                     name.substring(index)
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
