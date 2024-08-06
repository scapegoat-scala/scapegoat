package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class ListSizeTest extends InspectionTest {

  override val inspections = Seq[Inspection](new ListSize)

  "list.size use" - {
    "should report warning" - {
      val code = """class Test {
                     List(1,2,3).size
                     val a = List("sam")
                     a.size
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
