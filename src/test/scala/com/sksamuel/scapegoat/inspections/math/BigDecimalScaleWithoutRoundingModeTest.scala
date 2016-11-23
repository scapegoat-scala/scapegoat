package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

class BigDecimalScaleWithoutRoundingModeTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new BigDecimalScaleWithoutRoundingMode)

  "BigDecimalScaleWithoutRoundingMode" - {
    "should report warning" - {
      "for use of setScale without rounding mode" in {
        val code =
          """class Test {
            |  val b = BigDecimal(10)
            |  b.setScale(2)
            |} """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for use of setScale with rounding mode" in {
        val code =
          """class Test {
            |  import scala.math.BigDecimal.RoundingMode
            |  val b = BigDecimal(10)
            |  b.setScale(2, RoundingMode.HALF_UP)
            |} """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
