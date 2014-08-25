package com.sksamuel.scapegoat.inspections.inferrence

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.inference.PointlessTypeBounds
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class PointlessTypeBoundsTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new PointlessTypeBounds)

  "PointlessTypeBounds" - {
    "should report warning" - {
      "for class with Nothing lower bound" in {

        val code =
          """class Test[A >: Nothing]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for class with Any upper bound" in {

        val code =
          """class Test[A <: Any]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for method with Any upper bound" in {

        val code =
          """object Test {
            |  def foo[A <: Any] = {}
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for method with Nothing lower bound" in {

        val code =
          """object Test {
            |  def foo[A >: Nothing] = {}
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for implicit class with Nothing lower bound" in {

        val code =
          """class Test {
            |  implicit class Monster[T >: Nothing](t:T) {
            |   def bite : T = t
            |  }
            |  "bfg".bite
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for implicit class with Any upper bound" in {

        val code =
          """class Test {
            |  implicit class Monster[T <: Any](t:T) {
            |   def bite : T = t
            |  }
            |  "bfg".bite
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for class with upper bound only" in {

        val code =
          """class Test[A <: Exception]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for class with no type bound" in {

        val code =
          """class Test""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for class with multiple type params" in {

        val code =
          """class Test[A,B]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for class with multiple type params and single bounds" in {

        val code =
          """trait Foo
            |class Test[A,B <: Foo]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for class with multiple type params and multiple bounds" in {

        val code =
          """package com.sammy
            |trait Foo
            |trait Boo
            |class Test[A >: Boo,B <: Foo]""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for method with declared lower bound" in {

        val code =
          """object Test {
            |  def foo[A >: String] = {}
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for method with upper bound only" in {

        val code =
          """object Test {
            |  def foo[B <: Thread] = {}
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
      "for implicit class with no type bound" in {

        val code =
          """class Test {
            |  implicit class Monster[T](t:T) {
            |   def bite : T = t
            |  }
            |  "bfg".bite
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for higher kinded type bound" in {
        val code =
          """package com.sam
             import scala.language.higherKinds
             class A { def f[CC[X] <: Traversable[X], A](x: CC[A]) = {} }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
