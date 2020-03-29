package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class CollectionNegativeIndexTest extends InspectionTest {

  override val inspections = Seq(new CollectionNegativeIndex)

  "collection negative index" - {
    "should report warning" in {
      val code = """object Test {
                      List(1,2,3)(-2)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
