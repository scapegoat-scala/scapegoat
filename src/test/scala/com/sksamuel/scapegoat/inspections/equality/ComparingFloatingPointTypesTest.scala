package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class ComparingFloatingPointTypesTest extends InspectionTest {

  override val inspections = Seq(new ComparingFloatingPointTypes)

  "comparing floating type inspection" - {
    "should report warning" - {
      "for double comparison" in {
        val code = """class Test {
                      def compareFloats : Boolean = {
                        val a = 14.5
                        val b = 15.6
                        a == b
                        a != b
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
      "for float comparison" in {
        val code = """class Test {
                      def compareFloats : Boolean = {
                        val a = 14.5f
                        val b = 15.6f
                        a == b
                        a != b
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
    }
  }
}
