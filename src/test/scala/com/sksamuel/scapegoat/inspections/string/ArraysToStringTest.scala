package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class ArraysToStringTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new ArraysToString)

  "ArraysToString" - {
    "should report warning" - {
      "for .toString on an Array literal" in {
        val code =
          """class Test {
             Array(5).toString
           } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for .toString on an Array reference" in {
        val code =
          """class Test {
             val array = Array(1,2,3,4)
             array.toString
           } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for .toString on a List literal" in {
        val code =
          """class Test {
             List(5).toString
           } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for .toString on a List reference" in {
        val code =
          """class Test {
             val l = List(1,2,3,4)
             l.toString
           } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
