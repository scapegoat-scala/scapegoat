package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class CollectionPromotionToAnyTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new CollectionPromotionToAny)

  "lists using colon add with list" - {
    "should report warning" in {
      val code = """object Test {
                   |  val a = List(1, 2, 3)
                   |  val b = List(4, 5, 6)
                   |  val c = a :+ b
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "seqs using colon add with list" - {
    "should report warning" in {
      val code = """object Test {
                   |  val a = Seq(1, 2, 3)
                   |  val b = List(4, 5, 6)
                   |  val c = a :+ b
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "lists using colon add with seq" - {
    "should report warning" in {
      val code = """object Test {
                   |  val a = List(1, 2, 3)
                   |  val b = Seq(4, 5, 6)
                   |  val c = a :+ b
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "seqs using colon add with seq" - {
    "should report warning" in {
      val code = """object Test {
                   |  val a = Seq(1, 2, 3)
                   |  val b = Seq(4, 5, 6)
                   |  val c = a :+ b
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "Vectors using colon add with non seq" - {
    "should not report warning" in {
      val code = """object Test {
                   |        val a = Vector(1, 2, 3)
                   |        val b = 6
                   |        val c = a :+ b
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }

  "lists using colon add with non seq" - {
    "should not report warning" in {
      val code = """object Test {
                   |        val a = List(1, 2, 3)
                   |        val b = 4
                   |        val c = a :+ b
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }

  "list[any] using colon add" - {
    "should not report warning" in {
      val code = """object Test {
                   |        val a = List[Any](1, 2, 3)
                   |        val b = "string"
                   |        val c = a :+ b
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
