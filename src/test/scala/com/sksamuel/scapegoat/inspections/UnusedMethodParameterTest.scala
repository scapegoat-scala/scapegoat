package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class UnusedMethodParameterTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

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
  }
}
