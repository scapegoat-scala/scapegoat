package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class ComparingFloatingPointTypesTest
    extends AnyFreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

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
