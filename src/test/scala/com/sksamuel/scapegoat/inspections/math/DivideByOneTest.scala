package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class DivideByOneTest
    extends AnyFreeSpec
    with PluginRunner
    with Matchers
    with OneInstancePerTest {

  override val inspections = Seq(new DivideByOne)

  "divide by one" - {
    "for int" - {
      "should report warning" in {
        val code = """object Test {
                       val a = 14
                       val b = a / 1
                    } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "for float" - {
      "should report warning" in {
        val code = """object Test {
                       val c = 10.0
                       val d = c / 1
                    } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "for long" - {
      "should report warning" in {
        val code = """object Test {
                       val e = 100l
                       val f = e / 1
                    } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "for double" - {
      "should report warning" in {
        val code = """object Test {
                       val g = 5.0d
                       val h = g / 1
                    } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}
