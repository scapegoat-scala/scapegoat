package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class CatchNpeInspectionTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(CatchNpeInspection)

  "catching null pointer exception" - {
    "should report warning" in {

      val code = """object Test {
                        try {
                        var s : String = null
                        s.toString
                        } catch {
                        case e : NullPointerException =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }

    val code = """object Test {
                        try {
                        var s : String = null
                        s.toString
                        } catch {
                        case e : RuntimeException =>
                        }
                    } """.stripMargin

    compileCodeSnippet(code)
    compiler.scapegoat.reporter.warnings.size shouldBe 0
  }
}
