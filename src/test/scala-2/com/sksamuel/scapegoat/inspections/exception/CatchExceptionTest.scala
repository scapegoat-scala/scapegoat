package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Marconi Lanna */
class CatchExceptionTest extends InspectionTest {

  override val inspections = Seq[Inspection](new CatchException)

  "catch _ exception" - {
    "should report warning" in {

      val code1 = """object Test {
                        try {
                        } catch {
                          case t : Throwable =>
                          case _ : Exception =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code1)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "catch e exception" - {
    "should report warning" in {

      val code2 = """object Test {
                        try {
                        } catch {
                          case e : RuntimeException =>
                          case x : Exception =>
                          case f : Throwable =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code2)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "catch without exception case" - {
    "should not report warning" in {

      val code2 = """object Test {
                        try {
                        } catch {
                          case e : RuntimeException =>
                          case f : Throwable =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code2)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
