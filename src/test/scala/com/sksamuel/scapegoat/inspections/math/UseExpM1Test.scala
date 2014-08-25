package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Matic Potočnik */
class UseExpM1Test extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new UseExpM1)

  "using exp(x) - 1 instead of expm1(x)" - {
    "should report warning" in {

      val code = """object Test {
                        val a = 2d
                        math.exp(a) - 1
                        Math.exp(a) - 1
                        StrictMath.exp(a) - 1
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }
  }
}
