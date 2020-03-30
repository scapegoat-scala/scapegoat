package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.InspectionTest
class UseSqrtTest extends InspectionTest {

  override val inspections = Seq(new UseSqrt)

  "using pow instead of sqrt" - {
    "should report warning" in {

      val code = """object Test {
                        val a = 2
                        scala.math.pow(2, 0.5) // scala
                        scala.math.pow(2, 1/2d) // scala
                        math.pow(2, 0.5) // scala
                        math.pow(2, 1/2d) // scala
                        Math.pow(2, 1/2d) // java
                        StrictMath.pow(2, 1/2d) // java
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 6
    }
  }
}
