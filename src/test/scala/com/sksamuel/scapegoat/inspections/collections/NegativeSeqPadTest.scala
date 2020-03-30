package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class NegativeSeqPadTest extends InspectionTest {

  override val inspections = Seq(new NegativeSeqPad)

  "self assignment" - {
    "should report warning" - {
      val code = """class Test {
                     Seq(1,2,3).padTo(-1, 4)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
