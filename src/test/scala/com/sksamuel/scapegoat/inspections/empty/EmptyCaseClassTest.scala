package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.EmptyCaseClass
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class EmptyCaseClassTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new EmptyCaseClass)

  "empty class classes" - {
    "should report warning" in {

      val code = """object Test {
                      case class Empty()
                    }
                 """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
    "should not report warning" - {
      "for empty classes" in {
        val code = """object Test {
                      class Empty()
                    }
                   """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for none empty case classes" in {
        val code = """object Test {
                      case class Empty(name:String)
                    }
                   """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for case objects" in {
        val code = """object Test {
                      case object Empty
                    }
                   """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
