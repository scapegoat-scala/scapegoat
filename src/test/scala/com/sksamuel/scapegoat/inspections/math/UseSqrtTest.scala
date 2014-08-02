package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class UseSqrtTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new UseSqrt)

  "broken odd use" - {
    "should report warning" in {

      val code = """object Test {
                        val a = 2
                        Math.pow(2, 0.5)
                        Math.pow(2, 1/2d)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
