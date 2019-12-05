package com.sksamuel.scapegoat.inspections.controlflow

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

class RepeatedIfElseBodyTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new RepeatedIfElseBody)

  "repeated if else body" - {
    "should report warning" - {
      "for repeated code in both if branches" in {

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