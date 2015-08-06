package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.test.ScapegoatTestPluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Matic Potočnik */
class UseLog1PTest extends FreeSpec with Matchers with ScapegoatTestPluginRunner {

  override val inspections = Seq(new UseLog1P)

  "using log(x + 1) instead of log1p(x)" - {
    "should report warning" in {

      val code = """object Test {
                        val a = 2d
                        math.log(a + 1)
                        math.log(1 + a)
                        Math.log(a + 1)
                        StrictMath.log(a + 1)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 4
    }
  }
}
