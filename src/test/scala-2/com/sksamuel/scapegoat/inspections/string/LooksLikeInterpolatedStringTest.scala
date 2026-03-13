package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{Inspection, InspectionTest}
import org.scalatest.prop.TableDrivenPropertyChecks
class LooksLikeInterpolatedStringTest extends InspectionTest with TableDrivenPropertyChecks {

  override val inspections = Seq[Inspection](new LooksLikeInterpolatedString)

  private def singleQuote(n: String) = "\"" + n + "\""
  private def tripleQuote(n: String) = "\"\"\"" + n + "\"\"\""

  private val quoteTypes = Table(
    ("quote type", "quote function"),
    ("single quoted", singleQuote(_)),
    ("triple quoted", tripleQuote(_))
  )

  forAll(quoteTypes) { case (quoteType, quote) =>
    s"LooksLikeInterpolatedString ($quoteType)" - {
      "should report warning" - {
        "for string containing $var" in {
          val code =
            s"""object Test {
                      val str = ${quote("this is my $interpolated string lookalike")}
                   } """.stripMargin
          compileCodeSnippet(code)
          compiler.scapegoat.feedback.warnings.size shouldBe 1
        }
        "for string containing ${var.method}" in {
          val code =
            s"""object Test {
                      val str = ${quote("this is my ${interpolated.string} lookalike")}
                   } """.stripMargin
          compileCodeSnippet(code)
          compiler.scapegoat.feedback.warnings.size shouldBe 1
        }
      }
      "should not report warning" - {
        "for normal string" in {
          val code = s"""object Test {
                      val str = ${quote("this is my not interpolated string lookalike")}
                   } """.stripMargin
          compileCodeSnippet(code)
          compiler.scapegoat.feedback.warnings.size shouldBe 0
        }
        "for interpolated strings" in {
          val code = s"""object Test {
                      val a = "hello"
                      val str = s${quote("${a}")}
                   } """.stripMargin
          compileCodeSnippet(code)
          compiler.scapegoat.feedback.warnings.size shouldBe 0
        }
        "for quasi quotes" in {
          val code =
            s"""object Test {
                      val a = "hello"
                      implicit class QQuotes(val sc: StringContext) extends AnyVal {
                         def q(args: Any*): String = "anything"
                       }
                      val str = q${quote("${a}")}
                   } """.stripMargin
          compileCodeSnippet(code)
          compiler.scapegoat.feedback.warnings.size shouldBe 0
        }
        "for strings containing $anonfun" in {
          val code =
            s"""
               |  object Test {
               |     val str = ${quote("this contains $anonfun")}
               |  }
        """.stripMargin
          compileCodeSnippet(code)
          compiler.scapegoat.feedback.warnings.size shouldBe 0
        }
        "for strings with escaped $" in {
          val code =
            s"""
               |  object Test {
               |     val answer = 42
               |     val message = ${quote("What is the $$answer")}
               |  }
        """.stripMargin
          compileCodeSnippet(code)
          compiler.scapegoat.feedback.warnings.size shouldBe 0
        }
      }
    }
  }
}
