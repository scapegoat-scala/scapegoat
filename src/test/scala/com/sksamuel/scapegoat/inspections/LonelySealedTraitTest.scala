package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class LonelySealedTraitTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new LonelySealedTrait)

  "LonelySealedTrait" - {
    "should report warning" - {
      "when a sealed trait has no implementations" in {
        val code = """sealed trait ATeam"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "when a sealed trait has object implementation" in {

        val code =
          """sealed trait ATeam
             object Hannibal extends ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has case object implementation" in {

        val code =
          """sealed trait ATeam
             case object Hannibal extends ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has case object implementation with multiple parents" in {

        val code =
          """sealed trait ATeam
             case object Hannibal extends Serializable with ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has case class implementation with no parameters" in {

        val code =
          """sealed trait ATeam
             case class Hannibal() extends ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has class implementation with no parameters" in {

        val code =
          """sealed trait ATeam
             class Hannibal extends ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has case class implementation with multiple parents" in {

        val code =
          """sealed trait ATeam
             case class Hannibal(name:String) extends Serializable with ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has class implementation with multiple parents" in {

        val code =
          """sealed trait ATeam
             class Hannibal(name:String) extends Serializable with ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has case class implementation with parameters" in {

        val code =
          """sealed trait ATeam
             case class Hannibal(name:String) extends ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has class implementation with parameters" in {

        val code =
          """sealed trait ATeam
             class Hannibal(name:String) extends ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
