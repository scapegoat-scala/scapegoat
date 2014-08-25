package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class ComparisonToEmptyListTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ComparisonToEmptyList)

  "ComparisonToEmptyList" - {
    "should report warning" - {
      "for comparing lhs to an empty list" in {

        val code = """object Test {
                        val a = List(1,2,3)
                        val b = a == List()
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for comparing rhs to an empty list" in {

        val code = """object Test {
                        val a = List(1,2,3)
                        val b = List() == a
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for comparing lhs to List.empty" in {

        val code = """object Test {
                        val a = List(1,2,3)
                        val b = a == List.empty
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for comparing rhs to List.empty" in {

        val code = """object Test {
                        val a = List(1,2,3)
                        val b = List.empty == a
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for comparing lhs to Nil" in {

        val code = """object Test {
                        val a = List(1,2,3)
                        val b = a == Nil
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for comparing rhs to Nil" in {

        val code = """object Test {
                        val a = List(1,2,3)
                        val b = Nil == a
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}