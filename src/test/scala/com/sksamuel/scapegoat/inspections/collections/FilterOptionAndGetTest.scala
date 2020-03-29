package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class FilterOptionAndGetTest extends InspectionTest {

  override val inspections = Seq(new FilterOptionAndGet)

  "filter then size" - {
    "should report warning" in {

      val code = """object Test {
                      val a = List(None, Some("sam")).filter(_.isDefined).map(_.get)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
