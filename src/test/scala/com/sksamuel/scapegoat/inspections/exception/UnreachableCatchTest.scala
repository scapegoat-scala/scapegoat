package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class UnreachableCatchTest extends InspectionTest {

  override val inspections = Seq(new UnreachableCatch)

  "unreachable catch" - {
    "should report warning" - {
      "for subtype after supertype" in {

        val code1 = """object Test {
                        try {
                        } catch {
                          case _ : Throwable =>
                          case e : Exception => // not reachable
                        }
                    } """.stripMargin

        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }

      "when unconditional catch is followed by conditional catch on the same type" in {
        val code = """object Test {
                        try {
                        } catch {
                          case e: RuntimeException =>
                          case e: RuntimeException if e.getMessage.contains("foo") =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for super type after sub type" in {

        val code2 = """object Test {
                        try {
                        } catch {
                          case e : RuntimeException =>
                          case f : Exception =>
                          case x : Throwable =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code2)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when conditional catch is followed by unconditional catch on the same type" in {
        val code = """object Test {
                        try {
                        } catch {
                          case e: RuntimeException if e.getMessage.contains("foo") =>
                          case e: RuntimeException =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when conditional catch is followed by conditional catch on the same type" in {
        val code = """object Test {
                        try {
                        } catch {
                          case e: RuntimeException if e.getMessage.contains("foo") =>
                          case e: RuntimeException if e.getMessage.contains("bar") =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
