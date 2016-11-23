package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

class MaxParameterTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new MaxParameters)

  "MaxParameters" - {
    "should report warning" - {
      "for methods with over 10 parameters" in {
        val code = """class Test {
                      def foo(a:String, b:Int, c:Int, d:String, e:String, f:Int, g:Int, h:Int, i:Int, j:Long, k:Double) = ()
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for curried methods with over 10 parameters" in {
        val code = """class Test {
                      def foo(a:String, b:Int)(c:Int, d:String, e:String, f:Int, g:Int, h:Int, i:Int, j:Long, k:Double) = ()
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for methods with 10 or less parameters" in {
        val code = """class Test {
                      def foo(a:String, b:Int, c:Int, d:String, e:String, f:Int, g:Int, h:Int, i:Int, j:Long) = ()
                     |def foo(a:String, b:Int, c:Int, d:String, e:String, f:Int, g:Int, h:Int, i:Int) = ()
                     |def foo(a:String, b:Int, c:Int, d:String, e:String, f:Int, g:Int, h:Int) = ()
                     |def foo(a:String, b:Int, c:Int) = ()
                     |def foo(a:String) = ()
                     } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for case classes" in {
        val code = """case class Foo(a:String, b:Int, c:Int, d:String, e:String, f:Int, g:Int, h:Int, i:Int, j:Long, k:Double)"""

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
