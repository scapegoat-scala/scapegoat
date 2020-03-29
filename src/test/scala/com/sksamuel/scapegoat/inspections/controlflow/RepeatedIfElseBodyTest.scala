package com.sksamuel.scapegoat.inspections.controlflow

import com.sksamuel.scapegoat.InspectionTest
class RepeatedIfElseBodyTest extends InspectionTest {

  override val inspections = Seq(new RepeatedIfElseBody)

  "repeated if else body" - {
    "should report warning" - {
      "for exact same code in both if branches" in {

        val code = """object Test {
                      val a = "input"
                      println(if (a.length > 3) {
                        "long string"
                      } else {
                        "long string"
                      })
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for both if branches starting with the same command" in {

        val code = """object Test {
                      val a = "input"
                      val len = a.length
                      if (1 > 0) {
                        val len = a.length
                        println(s"Length is $len")
                      } else {
                        val len = a.length
                        println("I won't say")
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }

    }
    "should not report warning" - {
      "for similar yet different code in both if branches" in {

        val code = """object Test {
                      val a = "input"
                      println(if (a.length > 3) {
                        "long string"
                      } else {
                        "short string"
                      })
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
