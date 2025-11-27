package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.{InspectionTest, Levels}

class ComparingFloatingPointTypesTest extends InspectionTest(classOf[ComparingFloatingPointTypes]) {

  "comparing floating point types" - {
    "should report warning for double comparison with ==" in {
      val code = """class Test {
                      def compareFloats: Boolean = {
                        val a = 14.5
                        val b = 15.6
                        a == b
                      }
                    }""".stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.errors.assertable shouldEqual Seq(
        warning(4, Levels.Error, Some("a == b"))
      ).assertable
    }

    "should report warning for double comparison with !=" in {
      val code = """class Test {
                      def compareFloats: Boolean = {
                        val a = 14.5
                        val b = 15.6
                        a != b
                      }
                    }""".stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.errors.assertable shouldEqual Seq(
        warning(4, Levels.Error, Some("a != b"))
      ).assertable
    }

    "should report warning for float comparison with ==" in {
      val code = """class Test {
                      def compareFloats: Boolean = {
                        val a = 14.5f
                        val b = 15.6f
                        a == b
                      }
                    }""".stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.errors.assertable shouldEqual Seq(
        warning(4, Levels.Error, Some("a == b"))
      ).assertable
    }

    "should report warning for float comparison with !=" in {
      val code = """class Test {
                      def compareFloats: Boolean = {
                        val a = 14.5f
                        val b = 15.6f
                        a != b
                      }
                    }""".stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.errors.assertable shouldEqual Seq(
        warning(4, Levels.Error, Some("a != b"))
      ).assertable
    }

    "should not report warning for integer comparison" in {
      val code = """class Test {
                      def compareInts: Boolean = {
                        val a = 14
                        val b = 15
                        a == b
                      }
                    }""".stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.errors.assertable shouldEqual Seq().assertable
    }
  }

}
