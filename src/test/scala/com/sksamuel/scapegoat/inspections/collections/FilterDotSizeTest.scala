package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class FilterDotSizeTest extends InspectionTest {

  override val inspections = Seq[Inspection](new FilterDotSize)

  "filter then size" - {
    "should report warning" in {

      val code = """object Test {
                      val list = List(1,2,3,4).filter(_ % 2 == 0).size
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
