package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class TraversableHeadTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new TraversableHead)

  "option.head use" - {
    "should report warning" in {

      val code = """class Test {
                      Seq("sam").head
                      List("sam").head
                      Vector("sam").head
                      Iterable("sam").head
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 4
    }
    "should suppress warnings when SuppressWarnings specified with all" in {

      val code = """@SuppressWarnings(Array("all"))
                    class Test {
                      Seq("sam").head
                      List("sam").head
                      Vector("sam").head
                      Iterable("sam").head
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "should suppress warnings when SuppressWarnings specified with name" in {

      val code = """@SuppressWarnings(Array("TraversableHead"))
                    class Test {
                      Seq("sam").head
                      List("sam").head
                      Vector("sam").head
                      Iterable("sam").head
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should suppress val-level warnings when SuppressWarnings specified with all" in {
      val code = """class Test {
                      @SuppressWarnings(Array("all"))
                      val a : String = List("sam").head
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should suppress val-level warnings when SuppressWarnings specified with name" in {
      val code = """class Test {
                      @SuppressWarnings(Array("TraversableHead"))
                      val a : String = List("sam").head
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should suppress def-level warnings when SuppressWarnings specified with all" in {
      val code = """class Test {
                      @SuppressWarnings(Array("all"))
                      def a : String = List("sam").head
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should suppress def-level warnings when SuppressWarnings specified with name" in {
      val code = """class Test {
                      @SuppressWarnings(Array("TraversableHead"))
                      def a : String = List("sam").head
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should not report warning" - {
      "for var args" in {
        val code = """class F(args:String*)""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
