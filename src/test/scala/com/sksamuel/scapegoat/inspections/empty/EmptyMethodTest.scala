package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class EmptyMethodTest extends AnyFreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new EmptyMethod)

  "empty method" - {
    "should report warning" in {
      val code = """object Test {
                      private def foo = { }
                      private def foo2 = true
                      private def foo3 = {
                        ()
                      }
                      private def foo4 = {
                        println("sammy")
                        ()
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
    "should not report warning" - {
      "for empty trait methods" in {
        val code = """trait A { def foo = () } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "for empty methods in public classes" in {
        val code =
          """
            |class Animal {
            |  def makeSound(): Unit = {}
            |}
            |
            |class Dog extends Animal {
            |  override def makeSound(): Unit = {
            |    println("Bark")
            |  }
            |}
            |
            |class Fish extends Animal {}
            |""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
