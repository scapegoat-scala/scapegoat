package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class FilterDotHeadOptionTest extends InspectionTest {

  override val inspections = Seq(new FilterDotHeadOption)

  "self assignment" - {
    "should report warning" in {
      val code = """class Test {
                     List(1,2,3).filter(_ < 0).headOption
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
