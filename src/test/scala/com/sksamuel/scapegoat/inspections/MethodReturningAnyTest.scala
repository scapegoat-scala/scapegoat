package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.inspections.inference.MethodReturningAny
import com.sksamuel.scapegoat.test.ScapegoatTestPluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class MethodReturningAnyTest
    extends FreeSpec
    with Matchers
    with ScapegoatTestPluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new MethodReturningAny)

  "MethodReturningAny" - {
    "should report warning" - {
      "for methods returning any" in {

        val code = """class Test {
                        def foo : Any = 1
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for methods returning <:< Any" in {

        val code =
          """class Test {
            | def foo : Int = 4
            | def boo : String = "sam"
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for overriden methods returning any" in {
        val code =
          """object T {
               trait A {
                 def foo : AnyRef = "foo"
               }
               class B extends A {
                 override def foo : AnyRef = "overriden foo"
               }
            |} """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}