package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{OneInstancePerTest, FreeSpec, Matchers}

/** @author Stephen Samuel */
class ComparingFloatingPointTypesInspectionTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(ComparingFloatingPointTypesInspection)

  "comparing floating type inspection" - {
    "should report warning" - {
      "for double comparison" in {
        val code = """class Test {
                      def compareFloats : Boolean = {
                        val a = 14.5
                        val b = 15.6
                        a == b
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.reporter.warnings.size shouldBe 1
      }
      "for float comparison" in {
        val code = """class Test {
                      def compareFloats : Boolean = {
                        val a = 14.5f
                        val b = 15.6f
                        a == b
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.reporter.warnings.size shouldBe 1
      }
    }
  }
}
