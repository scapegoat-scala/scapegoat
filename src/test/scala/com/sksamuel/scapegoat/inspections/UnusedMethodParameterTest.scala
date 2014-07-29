package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class UnusedMethodParameterTest
  extends FreeSpec with ASTSugar with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new UnusedMethodParameter)

  "UnusedMethodParameter" - {
    "should report warning" in {

      val code = """class Test {
                      val initstuff = "sammy"
                      def foo(a:String, b:Int, c:Int) {
                        println(b)
                        foo(a,b,b)
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
    "should ignore abstract method" in {

      val code = """abstract class Test {
                      def foo(name:String) : String
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 0
    }
    "should ignore @SuppressWarnings" in {

      val code = """class Test {
                      @SuppressWarnings(Array("I know this is broken"))
                      def foo(a:String, b:Int, c:Int) {
                        println(b)
                        foo(a,b,b)
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 0
    }
  }
}
