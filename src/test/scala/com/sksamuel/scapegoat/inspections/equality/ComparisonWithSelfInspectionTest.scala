package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Stephen Samuel */
class ComparisonWithSelfInspectionTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new ComparisonWithSelf)

  "ComparisonWithSelf" - {
    "should report warning" in {

      val code = """object Test {
                      val b = true
                      if (b == b) {
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
