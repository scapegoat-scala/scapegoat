package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class EmptyInterpolatedStringTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new EmptyInterpolatedString)

  "EmptyInterpolatedString" - {
    "should report warning" in {

      val code = """object Test {
                      val name = "sam"
                      s"hello name"
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "non empty interpolated string" - {
    "should not report warning" in {

      val code = """object Test {
                     val name = "sam"
                      s"hello $name"
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
