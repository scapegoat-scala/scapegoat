package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class CatchNpeTest extends InspectionTest {

  override val inspections = Seq[Inspection](new CatchNpe)

  "catching null pointer exception" - {
    "should report warning" in {

      val code1 = """object Test {
                        try {
                          var s : String = null
                          s.toString
                        } catch {
                          case e : NullPointerException =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code1)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "catching non npe" - {
    "should not report warning" in {

      val code2 = """object Test {
                        try {
                          var s : String = null
                          s.toString
                        } catch {
                          case e : RuntimeException =>
                        }
                    } """.stripMargin

      compileCodeSnippet(code2)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
