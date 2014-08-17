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
      "when a sealed trait has implementations mixed in the code" in {

        val code =
          """sealed trait ATeam
             sealed trait BTeam
             case class Hannibal(name:String) extends ATeam
             case class Faceman(name:String) extends BTeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait has parents" in {

        val code =
          """
            |trait AnalyzerFilter {
            |  def name: String
            |}
            |
            |trait AnalyzerFilterDefinition {
            |  def filterType: String
            |}
            |
            |sealed trait CharFilter extends AnalyzerFilter
            |
            |sealed trait CharFilterDefinition extends CharFilter with AnalyzerFilterDefinition
            |
            |case object HtmlStripCharFilter extends CharFilter {
            |  val name = "html_strip"
            |}
            |
            |case class MappingCharFilter(name: String, mappings: (String, String)*)
            |    extends CharFilterDefinition {
            |  val filterType = "mapping"
            |}
            |
            |case class PatternReplaceCharFilter(name: String, pattern: String, replacement: String)
            |    extends CharFilterDefinition {
            |  val filterType = "pattern_replace"
            |}
            |
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when a sealed trait is implemented in containing object" in {

        val code =
          """
            |    sealed abstract class MultiMode(val elastic: String)
            |    case object MultiMode {
            |      case object Min extends MultiMode("min")
            |      case object Max extends MultiMode("max")
            |      case object Sum extends MultiMode("sum")
            |      case object Avg extends MultiMode("avg")
            |    }
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }

  }
}
