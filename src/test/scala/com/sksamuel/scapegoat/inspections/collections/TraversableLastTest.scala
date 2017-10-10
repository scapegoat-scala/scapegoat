package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

class TraversableLastTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new TraversableLast)

  "option.last use" - {
    "should report warning" in {

      val code = """class Test {
                      Seq("sam").last
                      List("sam").last
                      Vector("sam").last
                      Iterable("sam").last
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 4
    }
    "should not report warning" - {
      "for var args" in {
        val code = """class F(args:String*)""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
