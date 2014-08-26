package com.sksamuel.scapegoat.inspections.names

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.naming.MethodNames

import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class MethodNamestest
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
    }
  }
}
