package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.inference.MethodReturningAny

/** @author Stephen Samuel */
class MethodReturningAnyTest extends InspectionTest {

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
      "for overridden methods returning any" in {
        val code =
          """object T {
               trait A {
                 def foo : AnyRef = "foo"
               }
               class B extends A {
                 override def foo : AnyRef = "overridden foo"
               }
            |} """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}
