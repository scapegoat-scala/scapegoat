package com.sksamuel.scapegoat.inspections.names

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.naming.MethodNames

import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class MethodNamesTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new MethodNames)

  "MethodNames" - {
    "should report warning" - {
      "for methods beginning with underscore" in {
        val code =
          """object A {
               def _a = 1
             }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for methods beginning with capital letter" in {
        val code =
          """object A {
               def B = true
             }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for camel case methods" in {
        val code =
          """object A {
               def camelCase = false
             }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for getter methods" in {
        val code =
          """object A {
               val Name = "sammy"
             }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for parameters" in {
        val code =
          """object A {
               def foo(Name:String) = {}
             }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for setter methods" in {
        val code =
          """object A {
               var Name = "sammy"
             }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for nested vals" in {
        val code =
          """object A {
               def foo = {
                 val AAAA = 1
                 println(AAAA)
               }
             }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for nested traits" in {
        val code = """
              object Test {
                trait Inner {
                  def valueStr: String = "test"
                }
              } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for nested classes" in {
        val code = """
              object Test {
                class Inner {
                  def valueStr: String = "test"
                }
              } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for nested objects" in {
        val code = """
              object Test {
                object Inner {
                  def valueStr: String = "test"
                }
              } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for setter methods (2)" in {
        val code = """
          object Test {
             def hallo: String = ""
             def hallo_=(x: String): Unit = {}
          }
          """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings should have size 0
      }
    }
  }
}
