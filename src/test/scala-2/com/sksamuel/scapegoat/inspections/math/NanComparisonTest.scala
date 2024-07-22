package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Inspection, InspectionTest}
class NanComparisonTest extends InspectionTest {

  override val inspections = Seq[Inspection](new NanComparison)

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
