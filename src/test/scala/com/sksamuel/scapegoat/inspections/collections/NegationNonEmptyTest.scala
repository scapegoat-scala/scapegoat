package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class NegationNonEmptyTest extends InspectionTest {

  override val inspections = Seq(new NegationNonEmpty)

  "!nonEmpty" - {
    "should report warning" in {
      val code = """object Test {
                      val list = List(1,2,3)
                      val empty = !list.nonEmpty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
