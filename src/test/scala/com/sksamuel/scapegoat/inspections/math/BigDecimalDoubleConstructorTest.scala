package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class BigDecimalDoubleConstructorTest extends InspectionTest {

  override val inspections = Seq(new BigDecimalDoubleConstructor)

  "big decimal double constructor" - {
    "should report warning" in {
      val code =
        """class Test {
          | BigDecimal(0.1d)
          | val d = 0.1d
          | BigDecimal(d)
          | BigDecimal(100) // ok not a double
          | new java.math.BigDecimal(0.5d) // check java ones
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }
  }
}
