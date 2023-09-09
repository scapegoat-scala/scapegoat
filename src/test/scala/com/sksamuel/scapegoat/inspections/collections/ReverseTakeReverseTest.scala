package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}
class ReverseTakeReverseTest extends InspectionTest {

  override val inspections = Seq[Inspection](new ReverseTakeReverse)

  "ReverseTakeReverse" - {
    "should report warning" in {
      val code = """class Test {
                     List(1,2,3).reverse.take(2).reverse
                     Array(1,2,3).reverse.take(1).reverse
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
