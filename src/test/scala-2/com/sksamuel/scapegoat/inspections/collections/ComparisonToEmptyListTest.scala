package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class ComparisonToEmptyListTest extends InspectionTest {

  override val inspections = Seq[Inspection](new ComparisonToEmptyList)

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
    "should not report warning" - {
      "for custom parameterless case class with type parameter on rhs" in {
        val code = """object Test {
                        case class Paramless[T]()

                        val a = List(1,2,3)
                        val b = a == Paramless()
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for custom parameterless case class with type parameter on lhs" in {
        val code = """object Test {
                        case class Paramless[T]()

                        val a = List(1,2,3)
                        val b = Paramless() == a
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
