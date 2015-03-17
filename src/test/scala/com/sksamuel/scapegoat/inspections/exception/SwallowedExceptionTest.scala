package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class SwallowedExceptionTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new SwallowedException)

  "SwallowedException" - {
    "should report warning" - {
      "for single exception not handled" in {

        val code1 = """object Test {
                        try {
                        } catch {
                          case e : Exception =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for multiple exceptions not handled" in {

        val code1 = """object Test {
                        try {
                        } catch {
                          case e : Exception =>
                          case e : Throwable =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
      "for mixed exceptions handled / not handled" in {

        val code1 = """object Test {
                        try {
                        } catch {
                          case e : Exception => println("a")
                          case e : Throwable =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for exceptions all handled" in {
        val code = """object Test {
                        try {
                        } catch {
                          case e : RuntimeException => println("a")
                          case f : Exception => println("b")
                          case x : Throwable => println("c")
                        }
                    } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for exception called ignored" in {
        val code = """object A { try { println() } catch { case ignored : Exception => } }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for exception called ignore" in {
        val code = """object A { try { println() } catch { case ignore : Exception => } }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}