package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers }

class UseLog1PTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new UseLog1P)

  "using log(x + 1) instead of log1p(x)" - {
    "should report warning" in {

      val code = """object Test {
                        val a = 2d
                        scala.math.log(a + 1)
                        math.log(a + 1)
                        scala.math.log(1 + a)
                        math.log(1 + a)
                        Math.log(a + 1)
                        StrictMath.log(a + 1)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 6
    }
  }
}
