package com.sksamuel.scapegoat.inspections.unnecessary

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unneccesary.UnnecessaryToInt
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class UnnecessaryToIntTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new UnnecessaryToInt)

  "Unnecessary to int" - {
    "should report warning" - {
      "when invoking toInt on an int" in {
        val code =
          """object Test {
            | val i = 4
            | val j = i.toInt
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }

      "when invoking toInt on an integer literal" in  {
        val code =
          """object Example extends App {
            |  val a = 3.toInt        // NullPointerException here (v1.3.6).
            |  val b = (10 / 5).toInt // NullPointerException here (v1.3.6).
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
    }

    "should not report warning" - {
      "when invoking toInt on a String" in {
        val code =
          """object Test {
            |  val s = "5"
            |  val t = s.toInt
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when invoking toInt on an Integer" in {
        val code =
          """object Test {
            | def test(i: java.lang.Integer) = {
            |   val t = i.toInt
            | }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
