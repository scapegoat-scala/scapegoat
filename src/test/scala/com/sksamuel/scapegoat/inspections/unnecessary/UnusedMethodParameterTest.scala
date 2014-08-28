package com.sksamuel.scapegoat.inspections.unnecessary

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unneccesary.UnusedMethodParameter

import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class UnusedMethodParameterTest
  extends FreeSpec
  with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new UnusedMethodParameter)

  "UnusedMethodParameter" - {
    "should report warning" - {
      "for unused parmaeters in concrete methods" in {
        val code = """class Test {
                        val initstuff = "sammy"
                        def foo(a:String, b:Int, c:Int) {
                          println(b)
                          foo(a,b,b)
                        }
                      } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
        compiler.scapegoat.feedback.warns.size shouldBe 1
      }
    }
    "should ignore @SuppressWarnings" in {

      val code = """class Test {
                      @SuppressWarnings(Array("all"))
                      def foo(a:String, b:Int, c:Int) {
                        println(b)
                        foo(a,b,b)
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "should not report warning" - {
      "for abstract methods" in {

        val code = """abstract class Test {
                      def foo(name:String) : String
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for methods not returning" in {

        val code = """class Test {
                     |  def foo(name:String) = throw new RuntimeException
                     |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for overriden method without override keyword" in {
        val code = """package com.sam
                      trait Foo {
                        def foo(name:String):String
                      }
                      object Fool extends Foo {
                        def foo(name:String) : String = "sam"
                      } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}

