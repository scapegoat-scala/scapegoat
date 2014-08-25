package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class VarClosureTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new VarClosure)

  "VarClosure" - {
    "should report warning" - {
      "for closing over var" in {

        val code =
          """object Test {
                      var a = 1
                      val b = List(1,2,3)
                      val c = b.filter(_ < a)
                    }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for closing over val" in {

        val code =
          """object Test {
                      val a = 1
                      val b = List(1,2,3)
                      val c = b.filter(_ < a)
                    }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for local (non-closed) var" in {

        val code =
          """object Test {
                      val b = List(1,2,3)
                      val c = b.filter(arg => {
                        var b = 2
                        b = b + 3
                        arg < b
                      })
                    }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for invoking method" in {

        val code =
          """object Test {
                      def a = 1
                      val b = List(1,2,3)
                      val c = b.filter(_ < a)
                    }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
