package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.InspectionTest

/** @author Matic Potočnik */
class UseCbrtTest extends InspectionTest {

  override val inspections = Seq(new UseCbrt)

  "using pow instead of cbrt" - {
    "should report warning" in {

      val code = """object Test {
                        val a = 2
                        scala.math.pow(2, 1/3d)
                        math.pow(2, 1/3d)
                        scala.math.pow(2, 1/3f)
                        math.pow(2, 1/3f)
                        Math.pow(2, 1/3d)
                        StrictMath.pow(2, 1/3d)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 6
    }
  }
}
