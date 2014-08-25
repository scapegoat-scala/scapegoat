package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class CatchThrowableTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new CatchThrowable)

  "catch _ throwable" - {
    "should report warning" in {

      val code1 = """object Test {
                        try {
                        } catch {
                          case e : Exception =>
                          case _ : Throwable =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code1)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "catch e throwable" - {
    "should report warning" in {

      val code2 = """object Test {
                        try {
                        } catch {
                          case e : RuntimeException =>
                          case x : Throwable =>
                          case f : Exception =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code2)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "catch without throwable case" - {
    "should not report warning" in {

      val code2 = """object Test {
                        try {
                        } catch {
                          case e : RuntimeException =>
                          case f : Exception =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code2)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
