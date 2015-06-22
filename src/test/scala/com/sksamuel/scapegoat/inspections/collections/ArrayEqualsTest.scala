package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class ArrayEqualsTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ArrayEquals)

  "ArrayEquals" - {
    "should report warning" - {
      "for comparing two arrays using ==" in {

        val code = """class Test {
                     val a = Array(1,2,3)
                     val b = Array(1,2,3)
                     println(a == b)
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }

      "for comparing two arrays using !=" in {

        val code = """class Test {
                     val a = Array(1,2,3)
                     val b = Array(1,2,3)
                     println(a != b)
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for comparing two arrays using deep" in {

        val code = """class Test {
                     val a = Array(1,2,3)
                     val b = Array(1,2,3)
                     println(a.deep == b.deep)
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for comparing an array to null" in {

        val code = """class Test {
                     val a = Array(1,2,3)
                     println(a != null)
                     println(a == null)
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for arrays parameters to a case class" in {
        val code = """case class StemmerOverrideTokenFilter(name: String, rules: Array[String])"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
