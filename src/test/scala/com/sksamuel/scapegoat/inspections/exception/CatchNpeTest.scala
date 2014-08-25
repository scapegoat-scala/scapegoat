package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class CatchNpeTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new CatchNpe)

  "catching null pointer exception" - {
    "should report warning" in {

      val code1 = """object Test {
                        try {
                          var s : String = null
                          s.toString
                        } catch {
                          case e : NullPointerException =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code1)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "catching non npe" - {
    "should not report warning" in {

      val code2 = """object Test {
                        try {
                          var s : String = null
                          s.toString
                        } catch {
                          case e : RuntimeException =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code2)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}