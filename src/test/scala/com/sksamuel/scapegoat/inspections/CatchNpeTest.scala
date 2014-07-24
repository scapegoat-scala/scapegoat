package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class CatchNpeTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

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
      compiler.scapegoat.reporter.warnings.size shouldBe 1

      val code2 = """object Test {
                        try {
                        var s : String = null
                        s.toString
                        } catch {
                        case e : RuntimeException =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code2)
      compiler.scapegoat.reporter.warnings.size shouldBe 0
    }
  }
}
