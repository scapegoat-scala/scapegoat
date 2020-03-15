package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class LooksLikeInterpolatedStringTest
    extends AnyFreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new LooksLikeInterpolatedString)

  "LooksLikeInterpolatedString" - {
    "should report warning" - {
      "for string containing $var" in {
        val code = """object Test {
                      val str = "this is my $interpolated string lookalike"
                   } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "for string containing ${var.method}" in {
      val code = """object Test {
                      val str = "this is my ${interpolated.string} lookalike"
                   } """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "should not report warning" - {
    "for normal string" in {
      val code = """object Test {
                      val str = "this is my not interpolated string lookalike"
                   } """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "for interpolated strings" in {
      val code = """object Test {
                      val a = "hello"
                      val str = s"${a}"
                   } """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "for quasi quotes" in {
      val code =
        """object Test {
                      val a = "hello"
                      implicit class QQuotes(val sc: StringContext) extends AnyVal {
                         def q(args: Any*): String = "anything"
                       }
                      val str = q"${a}"
                   } """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "for strings containing $anonfun" in {
      val code =
        """
          |  object Test {
          |     val str = "this contains $anonfun"
          |  }
        """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "for strings with escaped $" in {
      val code =
        """
          |  object Test {
          |     val answer = 42
          |     val message = s"$$answer = $answer"
          |  }
        """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
