package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.controlflow.WhileTrue

/** @author Stephen Samuel */
class WhileTrueTest extends InspectionTest {

  override val inspections = Seq(new WhileTrue)

  "while loop" - {
    "when constant" - {
      "should report warning" in {

        val code = """
                    object Test {
                      while (true) {
                        println("sam")
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "when not constant" - {
      "should not report warning" in {

        val code = """
                    object Test {
                      while (System.currentTimeMillis > 0) {
                        println("sam")
                      }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }

  "do while loop" - {
    "when constant" - {
      "should report warning" in {

        val code = """
                    object Test {
                      do {
                        println("sam")
                      } while (true)
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "when not constant" - {
      "should not report warning" in {

        val code = """
                    object Test {
                      do {
                        println("sam")
                      } while (System.currentTimeMillis > 0)
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
