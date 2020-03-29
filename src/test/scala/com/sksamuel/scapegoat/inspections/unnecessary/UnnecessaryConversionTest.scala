package com.sksamuel.scapegoat.inspections.unnecessary

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.unneccesary.UnnecessaryConversion

/** @author Stephen Samuel */
class UnnecessaryConversionTest extends InspectionTest {

  override val inspections = Seq(new UnnecessaryConversion)

  "Unnecessary conversion" - {
    "should report warning" - {
      "when invoking toString on a String" in {

        val code =
          """object Test {
                      val i = "sam"
                      val j = i.toString
                    }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when invoking toInt on an int" in {
        val code =
          """object Test {
            | val i = 4
            | val j = i.toInt
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when invoking toInt on an integer literal" in {
        val code =
          """object Example extends App {
            |  val a = 3.toInt        // NullPointerException here (v1.3.6).
            |  val b = (10 / 5).toInt // NullPointerException here (v1.3.6).
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
      "when invoking toLong on a long" in {
        val code =
          """object Test {
            | val i: Long = 436
            | val j = i.toLong
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when invoking toLong on a Long literal" in {
        val code =
          """object Example extends App {
            |  val a = 123456789012L
            |  val b = a.toLong
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when invoking toList on a list" in {
        val code =
          """object Test {
            | val list = List(1,2,3)
            | val something = list.toList
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when invoking toSet on a set" in {
        val code =
          """object Test {
            | val set = Set(4,3,6)
            | val something = set.toSet
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when invoking toSeq on a seq" in {
        val code =
          """object Test {
            | val seq = Seq(4,3,6)
            | val something = seq.toSeq
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }

    "should not report warning" - {
      "when invoking toString on a BigDecimal" in {
        val code =
          """object Test {
                      val s = BigDecimal(5)
                      val t = s.toString
                    }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
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
      "when invoking toLong on a int" in {
        val code =
          """object Test {
            | val i: Int = 436
            | val j = i.toLong
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when invoking toSeq on a set" in {
        val code =
          """object Test {
            | val set = Set(4,3,6)
            | val something = set.toSeq
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when invoking toSet on a list or seq" in {
        val code =
          """object Test {
            | val list = List(4,3,6)
            | val something = list.toSet
            | val seq = Seq(1,3,6)
            | val thing = seq.toSet
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

    }
  }
}
