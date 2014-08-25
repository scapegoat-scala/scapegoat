package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class ParameterlessMethodReturnsUnitTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ParameterlessMethodReturnsUnit)

  "ParameterlessMethodReturnsUnit" - {
    "should report warning" - {
      "for methods with unit and no params" in {

        val code =
          """object Test {
               def paramless: Unit = ()
             } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for methods with unit and params" in {

        val code =
          """object Test {
               def params(): Unit = ()
             } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for parameterless non unit methods" in {

        val code =
          """object Test {
               def params: Int = 3
             } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
