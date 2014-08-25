package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class ComparisonToEmptySetTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ComparisonToEmptySet)

  "ComparisonToEmptySet" - {
    "should report warning" - {
      "for comparing lhs to an empty set" in {

        val code = """object Test {
                        val a = Set(1,2,3)
                        val b = a == Set()
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for comparing rhs to an empty set" in {

        val code = """object Test {
                        val a = Set(1,2,3)
                        val b = Set() == a
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for comparing lhs to Set.empty" in {

        val code = """object Test {
                        val a = Set(1,2,3)
                        val b = a == Set.empty
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for comparing rhs to Set.empty" in {

        val code = """object Test {
                        val a = Set(1,2,3)
                        val b = Set.empty == a
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}