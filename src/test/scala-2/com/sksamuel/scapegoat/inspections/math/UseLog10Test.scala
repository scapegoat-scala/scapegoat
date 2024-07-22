package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

class UseLog10Test extends InspectionTest {

  override val inspections = Seq[Inspection](new UseLog10)

  "using log(x)/log(10) instead of log10(x)" - {
    "should report warning" in {

      val code = """object Test {
                        val a = 2
                        scala.math.log(a)/scala.math.log(10)
                        math.log(a)/math.log(10)
                        scala.math.log(a+4)/scala.math.log(10d)
                        math.log(a+4)/math.log(10d)
                        Math.log(a)/Math.log(10)
                        StrictMath.log(a)/StrictMath.log(10)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 6
    }
  }
}
