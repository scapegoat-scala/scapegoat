package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.test.ScapegoatTestPluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Matic Potočnik */
class UseCbrtTest extends FreeSpec with Matchers with ScapegoatTestPluginRunner {

  override val inspections = Seq(new UseCbrt)

  "using pow instead of cbrt" - {
    "should report warning" in {

      val code = """object Test {
                        val a = 2
                        math.pow(2, 1/3d)
                        math.pow(2, 1/3f)
                        Math.pow(2, 1/3d)
                        StrictMath.pow(2, 1/3d)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 4
    }
  }
}
