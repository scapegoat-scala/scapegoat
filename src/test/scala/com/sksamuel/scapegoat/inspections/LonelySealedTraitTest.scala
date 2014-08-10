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

        val code =
          """
             sealed trait ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "when a sealed trait has implementations" in {

        val code =
          """
             sealed trait ATeam
             case class Hannibal() extends ATeam
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
