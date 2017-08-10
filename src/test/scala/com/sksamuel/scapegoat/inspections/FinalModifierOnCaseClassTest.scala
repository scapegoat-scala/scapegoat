package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

class FinalModifierOnCaseClassTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new FinalModifierOnCaseClass)

  private def assertFinalModOnCaseClass(code: String): Unit = {
    compileCodeSnippet(code)
    compiler.scapegoat.feedback.warnings.size shouldBe 1
    compiler.scapegoat.feedback.warnings.head.text shouldBe ("Missing final modifier on case class")
  }

  private def assertNoFinalModOnCaseClass(code: String): Unit = {
    compileCodeSnippet(code)
    compiler.scapegoat.feedback.warnings should be ('empty)
  }

  "Missing final modifier on case class" - {
    "should report warning" - {
      "when used as a top level definition" in {
        val code = """case class Person(name: String)"""

        assertFinalModOnCaseClass(code)
      }

      "when used within an object definition" in {
        val code = """object Test{
                      |  case class Person(name: String)
                      }""".stripMargin

        assertFinalModOnCaseClass(code)
      }
    }

    "should not report warning" - {
      "when used on a abstract case class" in {
        val code = """abstract case class Person(name: String)"""

        assertNoFinalModOnCaseClass(code)
      }

      "when used on issue example" in {
        val code = """object Test {
                      |  sealed abstract case class Nat(toInt: Int)
                      |  object Nat {
                      |    def fromInt(n: Int): Option[Nat] =
                      |      if (n >= 0) Some(new Nat(n) {}) else None
                      |  }
                      }""".stripMargin

        assertNoFinalModOnCaseClass(code)
      }
    }
  }
}
