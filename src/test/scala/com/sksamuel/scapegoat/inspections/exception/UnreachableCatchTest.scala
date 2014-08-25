package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class UnreachableCatchTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new UnreachableCatch)

  "unreachable catch" - {
    "should report warning" - {
      "for subtype after supertype" in {

        val code1 = """object Test {
                        try {
                        } catch {
                          case _ : Throwable =>
                          case e : Exception => // not reachable
                        }
                    } """.stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for super type after sub type" in {

        val code2 = """object Test {
                        try {
                        } catch {
                          case e : RuntimeException =>
                          case f : Exception =>
                          case x : Throwable =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code2)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}