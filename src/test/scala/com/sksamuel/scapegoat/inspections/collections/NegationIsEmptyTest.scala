package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class NegationIsEmptyTest extends InspectionTest {

  override val inspections = Seq(new NegationIsEmpty)

  "!isEmpty" - {
    "should report warning" in {
      val code = """object Test {
                      val list = List(1,2,3)
                      val notEmpty = !list.isEmpty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
