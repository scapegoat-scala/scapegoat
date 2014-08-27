package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class RepeatedCaseBodyTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new RepeatedCaseBody)

  "repeated case bodies" - {
    "should report warning" - {
      "for repeated no-guard cases" in {

        val code = """object Test {
                      val s : Any = null
                      s match {
                       case "sam" => println("foo"); println("foo"); println("foo"); println("foo"); println("foo")
                       case "bam" => println("foo"); println("foo"); println("foo"); println("foo"); println("foo")
                       case _ =>
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for repeated no-guard cases with ignored guard cases" in {

        val code = """object Test {
                      val s : Any = null
                      s match {
                       case str : String if str.length == 3 => println("foo"); println("foo"); println("foo"); println("foo"); println("foo")
                       case str : String => println("foo"); println("foo"); println("foo"); println("foo"); println("foo")
                       case i : Int=> println("foo"); println("foo"); println("foo"); println("foo"); println("foo")
                       case _ =>
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for non repeated no guard cases" in {

        val code = """object Test {
                      val s = "sam"
                      s match {
                       case "sam" => println("foo"); println("foo"); println("foo")
                       case "bam" => println("foo"); println("boo"); println("foo")
                       case _ =>
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for repeated but guarded cases" in {

        val code = """object Test {
                      val s = "sam"
                      s match {
                       case s : String if s.length == 3 => println("foo"); println("foo"); println("foo")
                       case s : String => println("foo"); println("foo"); println("foo")
                       case _ =>
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for bodies with two or less statements" in {

        val code = """class T(a:String) {
                      val s = "sam"
                      s match {
                       case s : String => println("foo", a);
                       case _ => println("foo", a);
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}