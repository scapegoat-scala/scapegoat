package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class AvoidOperatorOverloadTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new AvoidOperatorOverload)

  "AvoidOperatorOverload" - {
    "should report warning" - {
      "for user defined nutjob method names" in {

        val code = """object Test {
                      def ::: = println("wtf")
                      def <<:>> = println("nutjob")
                      def !><! = println("omg")
                    }
                   """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 3
      }
    }
    "should not report warning" - {
      "for single character symbol method names" in {

        val code = """object Test {
                      def ! = println("fair nuff")
                      def ? = println("go on mate")
                    }
                   """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for double character symbol method names" in {

        val code = """object Test {
                      def !! = println("fair nuff")
                      def :: = println("go on mate")
                    }
                   """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for case classes synthetic methods" in {

        val code = """case class Test()""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for case class getter and setters" in {

        val code = """case class Test(name:String)""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for class fields" in {

        val code = """class Test(val name:String)""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for local vars" in {

        val code =
          """class Test {
            | var name : String = _
            |}
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for traits" in {
        val code = """trait A {
                     |  def foo(indices: String*) = ()
                     |}""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for case classes with parameters and extends trait" in {
        val code =
          """trait SymbolSearchResult {
            |  val name: String
            |  val localName: String
            |  val declaredAs: scala.Symbol
            |  val pos: Option[(String, Int)]
            |}
            |case class TypeSearchResult(name: String,
                localName: String,
                declaredAs: scala.Symbol,
                pos: Option[(String, Int)]) extends SymbolSearchResult""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
