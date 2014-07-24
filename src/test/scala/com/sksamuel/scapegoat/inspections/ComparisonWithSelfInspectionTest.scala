package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class ComparisonWithSelfInspectionTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(ComparisonWithSelf)

  "ComparisonWithSelf" - {
    "should report warning" in {

      val code = """object Test {
                      val b = true
                      if (b == b) {
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
  }
}
