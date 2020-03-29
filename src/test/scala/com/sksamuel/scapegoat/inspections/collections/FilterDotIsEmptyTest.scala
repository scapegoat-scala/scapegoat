package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class FilterDotIsEmptyTest extends InspectionTest {

  override val inspections = Seq(new FilterDotIsEmpty)

  "self assignment" - {
    "should report warning" in {
      val code = """class Test {
                     val empty = List(1,2,3).filter(_ < 0).isEmpty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
