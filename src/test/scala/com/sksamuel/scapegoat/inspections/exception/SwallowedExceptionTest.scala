package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class SwallowedExceptionTest extends InspectionTest {

  override val inspections = Seq(new SwallowedException)

  "SwallowedException" - {
    "should report warning" - {
      "for single exception not handled" in {

        val code1 = """object Test {
                        try {
                        } catch {
                          case e : Exception =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for multiple exceptions not handled" in {

        val code1 = """object Test {
                        try {
                        } catch {
                          case e : Exception =>
                          case e : Throwable =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
      "for mixed exceptions handled / not handled" in {

        val code1 = """object Test {
                        try {
                        } catch {
                          case e : Exception => println("a")
                          case e : Throwable =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when exception is masked" in {
        val code1 = """object Test {
                         try {
                           println(Integer.valueOf("asdf"))
                         } catch {
                           case nfe: NumberFormatException =>
                             throw new IllegalArgumentException("not a number")
                         }
                       }""".stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when original exception is logged but not re-thrown" in {
        val code1 = s"""object Test {
                         def error(e: Exception, str: String) = println("Log ERROR " + str + " " + e)
                         try {
                           println(Integer.valueOf("asdf"))
                         } catch {
                           case nfe2: NumberFormatException =>
                             error(nfe2, "Invalid format")
                             throw new IllegalStateException("it's not a number")
                         }
                       }""".stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }

    "should not report warning" - {
      "for all exceptions handled" in {
        val code = """object Test {
                        try {
                        } catch {
                          case e : RuntimeException => println("a")
                          case f : Exception => println("b")
                          case x : Throwable => println("c")
                        }
                    } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for exception called ignored" in {
        val code = """object A { try { println() } catch { case ignored : Exception => } }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for exception called ignore" in {
        val code = """object A { try { println() } catch { case ignore : Exception => } }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when exception is re-thrown with proper root cause" in {
        val code1 = """object Test {
                         try {
                           println(Integer.valueOf("asdf"))
                         } catch {
                           case nfe: NumberFormatException =>
                             throw new IllegalArgumentException("not a number", nfe)
                         }
                       }""".stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
