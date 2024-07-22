package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class AnyUseTest extends InspectionTest {

  override val inspections = Seq[Inspection](new AnyUse)

  "AnyUse" - {
    "should report warning" - {
      "for methods returning any" in {

        val code = """class Test {
                       def foo =  {
                         if (1 < System.currentTimeMillis) {
                           "sam"
                         } else {
                            4
                         }
                       }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for vals returning any" in {

        val code = """class Test {
                       val foo =  {
                         if (1 < System.currentTimeMillis) {
                           "sam"
                         } else {
                            4
                         }
                       }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for lazy vals returning any" in {

        val code = """class Test {
                       lazy val foo =  {
                         if (1 < System.currentTimeMillis) {
                           "sam"
                         } else {
                            4
                         }
                       }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for vars returning any" in {

        val code = """class Test {
                       var foo =  {
                         if (1 < System.currentTimeMillis) {
                           "sam"
                         } else {
                            4
                         }
                       }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}
