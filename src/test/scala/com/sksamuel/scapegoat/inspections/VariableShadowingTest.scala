package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class VariableShadowingTest extends AnyFreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new VariableShadowing)

  "VariableShadowing" - {
    "should report warning" - {
      "when variable shadows in nested def" in {
        val code =
          """class Test {
            |  def foo = {
            |    val a = 1
            |    def boo = {
            |      val a = 2
            |      println(a)
            |    }
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined in def shadows field" in {
        val code =
          """class Test {
            |  val a = 1
            |  def foo = {
            |    val a = 2
            |    println(a)
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined as case bind shadows field" in {
        val code =
          """class Test {
            |  val a = 1
            |  def foo(b : Int) = b match {
            |    case a => println(a)
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined in nested def in case shadows field" in {
        val code =
          """class Test {
            |  val a = 1
            |  def foo(b : Int) = b match {
            |    case f =>
            |     def boo() = {
            |       val a = 2
            |       println(a)
            |     }
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined in def shadows parameter" in {
        val code =
          """class Test {
            |  def foo(a : Int) = {
            |    val a = 1
            |    println(a)
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined in nested def shadows outer def parameter" in {
        val code =
          """class Test {
            |  def foo(a : Int) = {
            |    println(a)
            |    def boo = {
            |      val a = 2
            |      println(a)
            |    }
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "when sibling defs define same variable" in {
        val code =
          """class Test {
            |  def foo = {
            |    val a = 1
            |    println(a)
            |  }
            |  def boo = {
            |    val a = 2
            |    println(a)
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when two if branches define the same variable" in {
        val code =
          """class Test {
            |  if (1 > 0) {
            |    val something = 4
            |    println(something+1)
            |  } else {
            |    val something = 2
            |    println(something+2)
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when two sibling cases define the same local variable" in {
        val code =
          """class Test {
            |  val possibility: Option[Int] = Some(3) 
            |  possibility match {
            |    case Some(x) => 
            |      val y = x + 1    
            |      println(y)
            |    case None =>
            |      val y = 0
            |      println(y)     
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when visiting a match case, especially not visit it twice" in {
        val code =
          """class Test {
            |  val possibility: Option[Int] = Some(3)
            |  possibility match {
            |    case Some(x) =>
            |      val y = x + 1
            |      println(y)
            |    case None => println("None")
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
