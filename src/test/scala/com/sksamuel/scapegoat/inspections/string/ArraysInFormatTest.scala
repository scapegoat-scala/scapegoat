package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class ArraysInFormatTest extends InspectionTest {

  override val inspections = Seq(new ArraysInFormat)

  "use of array in format" - {
    "should report warning" in {
      val code =
        """class Test {
            val array = Array("sam")
            "%s".format(array)
            "%s".format(Array("inline"))
            "%s".format(List("I'm deep")) // list is fine
          } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
