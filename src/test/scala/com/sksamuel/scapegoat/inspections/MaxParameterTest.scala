package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{OneInstancePerTest, FreeSpec, Matchers}

/** @author Stephen Samuel */
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
      "for methods with 10 parameters" in {
        val code = """class Test {
                      def foo(a:String, b:Int, c:Int, d:String, e:String, f:Int, g:Int, h:Int, i:Int, j:Long) = ()
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
