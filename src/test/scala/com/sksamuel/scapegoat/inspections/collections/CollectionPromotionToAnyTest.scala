package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class CollectionPromotionToAnyTest
    extends AnyFreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new CollectionPromotionToAny)

  "CollectionPromotionToAny" - {
    "should report warning" - {
      "lists using colon add with list" in {
        val code = """object Test {
                     |  val a = List(1, 2, 3)
                     |  val b = List(4, 5, 6)
                     |  val c = a :+ b
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "seqs using colon add with list" in {
        val code = """object Test {
                     |  val a = Seq(1, 2, 3)
                     |  val b = List(4, 5, 6)
                     |  val c = a :+ b
                  } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "lists using colon add with seq" in {
        val code = """object Test {
                     |  val a = List(1, 2, 3)
                     |  val b = Seq(4, 5, 6)
                     |  val c = a :+ b
                  } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "seqs using colon add with seq" in {
        val code = """object Test {
                     |  val a = Seq(1, 2, 3)
                     |  val b = Seq(4, 5, 6)
                     |  val c = a :+ b
                  } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "Vectors using colon add with non seq" in {
        val code = """object Test {
                     |        val a = Vector(1, 2, 3)
                     |        val b = 6
                     |        val c = a :+ b
                  } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "lists using colon add with non seq" in {
        val code = """object Test {
                     |        val a = List(1, 2, 3)
                     |        val b = 4
                     |        val c = a :+ b
                  } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "list[any] using colon add" in {
        val code = """object Test {
                     |        val a = List[Any](1, 2, 3)
                     |        val b = "string"
                     |        val c = a :+ b
                  } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when adding a string to seq[Any]" in {
        val code = """object Test {
                     | val xs = Seq.empty[Any]
                     | println(xs :+ "hello")
                     |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
