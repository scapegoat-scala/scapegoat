package com.sksamuel.scapegoat.inspections.inferrence

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.inference.BoundedByFinalType
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class BoundedByFinalTypeTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new BoundedByFinalType)

  "BoundedByFinalType" - {
    "should report warning" - {
      "for class with pointless type bound" in {

        val code =
          """class Test[A <: String]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for method with pointless type bound" in {

        val code =
          """object Test {
            |  def foo[B <: Integer] = {}
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for class with non final type bound" in {

        val code =
          """class Test[A <: Exception]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for class with any type bound" in {

        val code =
          """class Test[A <: Any]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for class with no type bound" in {

        val code =
          """class Test""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for method with non final type bound" in {

        val code =
          """object Test {
            |  def foo[B <: Thread] = {}
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for method with any type bound" in {

        val code =
          """object Test {
            |  def foo[B <: Any] = {}
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for method with no type bound" in {

        val code =
          """object Test {
            |  def foo = {}
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
