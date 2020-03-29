package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class ListAppendTest extends InspectionTest {

  override val inspections = Seq(new ListAppend)

  "list.append use" - {
    "should report warning" - {
      val code = """class Test {
                     List(1,2,3) :+ 4
                     val a = List("list")
                     a :+ "oh no"
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
