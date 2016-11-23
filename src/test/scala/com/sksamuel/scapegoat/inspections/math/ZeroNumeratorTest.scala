package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

class ZeroNumeratorTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new ZeroNumerator)

  "zero numerator" - {
    "should report warning" - {
      "for 0 literals" in {
        val code =
          """class Test {
             val a :Long = System.currentTimeMillis
             val b : Int = 4
             val c = 0 / a
             0 / b
          } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
    }
    "should not report warning" - {
      "for getters in numerator" in {
        val code =
          """case class Person(name:String,age:Int)
             class Test {
               val p = Person("sam", 34)
               val a = p.age / 5
             }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
