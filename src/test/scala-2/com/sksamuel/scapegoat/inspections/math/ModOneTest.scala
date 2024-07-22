package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class ModOneTest extends InspectionTest {

  override val inspections = Seq[Inspection](new ModOne)

  "mod one use" - {
    "should report warning" in {

      val code = """object Test {
                     var i = 15
                     def odd(a: Int) = a % 1
                     val odd2 = i % 1
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
