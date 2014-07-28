package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class EmptySychronizedBlockTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new EmptyMethod)

  "empty empty" - {
    "should report warning" in {

      val code = """object Test {
                      def foo = { }
                      def foo2 = true
                      def foo3 = {
                        ()
                      }
                      def foo4 = {
                        "sammy"
                        ()
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 2
    }
  }
}
