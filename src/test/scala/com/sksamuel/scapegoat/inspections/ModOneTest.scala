package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.math.ModOne
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class ModOneTest extends FreeSpec with PluginRunner with Matchers {

  override val inspections = Seq(new ModOne)

  "mod one use" - {
    "should report warning" in {

      val code = """object Test {
                   |  var i = 15
                   |        def odd(a: Int) = a % 1
                   |        val odd2 = i % 1
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
