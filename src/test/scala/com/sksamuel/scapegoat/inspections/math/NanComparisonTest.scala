package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class NanComparisonTest
    extends AnyFreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new NanComparison)

  "nan comparison" - {
    "should report warning" - {
      val code =
        """class Test {
          |
          |  val d = 0.5d
          |  d == Double.NaN
          |  Double.NaN == d
          |
          |  val f = 0.5f
          |  f == Double.NaN
          |  Double.NaN == f
          |
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 4
    }
  }
}
