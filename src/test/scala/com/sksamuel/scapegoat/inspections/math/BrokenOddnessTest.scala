package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Stephen Samuel */
class BrokenOddnessTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new BrokenOddness)

  "broken odd use" - {
    "should report warning" in {

      val code = """object Test {
                   |var i = 15
                   |        def odd(a: Int) = a % 2 == 1
                   |        val odd2 = i % 2 == 1
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
