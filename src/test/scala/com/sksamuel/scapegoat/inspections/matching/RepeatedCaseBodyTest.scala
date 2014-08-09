package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class RepeatedCaseBodyTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new RepeatedCaseBody)

  "repeated case bodies" - {
    "should report warning" in {

      val code = """object Test {
                      val s = "sam"
                      s match {
                       case "sam" => println("foo")
                       case "bam" => println("foo")
                       case _ =>
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "non repeated case bodies" - {
    "should not report warning" in {

      val code = """object Test {
                      val s = "sam"
                      s match {
                       case "sam" => println("foo")
                       case "bam" => println("fool")
                       case _ =>
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
