package com.sksamuel.scapegoat

import com.sksamuel.scapegoat.inspections.option.OptionGet

class InspectionTraverserTest extends InspectionTest(classOf[OptionGet]) {
  "InspectionTraverser" - {
    "should ignore inspection based on SuppressWarnings on class" in {
      val code = """
      @SuppressWarnings(Array("all"))
      class Test {
        val o = Option("sammy")
        o.get
      }""".stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.errors.assertable shouldEqual Seq.empty
    }

    "should ignore inspection based on SuppressWarnings on method" in {
      val code = """
      class Test {
        @SuppressWarnings(Array("all"))
        def func(): String = {
          // ignored violation
          val o = Option("sammy")
          o.get
        }

        // violation
        val o2 = Option("sammy")
        o2.get

        func()
      }""".stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.errors.assertable shouldEqual Seq(
        warning(11, Levels.Error, Some("o2.get"))
      )
    }
  }
}
