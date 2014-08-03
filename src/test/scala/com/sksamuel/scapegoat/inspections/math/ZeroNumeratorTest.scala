package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class ZeroNumeratorTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new ZeroNumerator)

  "zero numerator" - {
    "should report warning" in {
      val code =
        """class Test {
             val a = System.currentTimeMillis
             val b = 0
             0 / a
             b / a
             b / 0
          } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }
  }
}
