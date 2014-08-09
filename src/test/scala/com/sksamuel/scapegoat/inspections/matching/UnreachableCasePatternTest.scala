package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class UnreachableCasePatternTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new UnreachableCasePattern)

  "repeated case patterns (without guards)" - {
    "should report warning" in {

      val code = """object Test {
                      val s = "sam"
                      s match {
                       case "sam" => println("foo")
                       case "bam" => println("foo")
                       case "sam" =>
                       case _ =>
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "repeated case patterns (with same guards)" - {
    "should report warning" in {

      val code = """object Test {
                      val s = "sam"
                      s match {
                       case "sam" => println("foo")
                       case "bam" => println("foo")
                       case "sam" if 1 < 0=>
                       case "sam" if 1 < 0 =>
                       case _ =>
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "repeated case patterns (with different guards)" - {
    "should not report warning" in {

      val code = """object Test {
                      val s = "sam"
                      s match {
                       case "sam" => println("foo")
                       case "bam" => println("foo")
                       case "sam" if 1 < 0=>
                       case _ =>
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
  "non repeated case patterns (without guards)" - {
    "should not report warning" in {

      val code = """object Test {
                      val s = "sam"
                      s match {
                       case "sam" => println("foo")
                       case "bam" => println("fool")
                       case "ham" =>
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
