package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.InspectionTest

class CatchExceptionImmediatelyRethrownTest extends InspectionTest {

  override val inspections = Seq(new CatchExceptionImmediatelyRethrown)

  "catch exception immediately throw" - {
    "should report warning" in {

      val testCode = """object Test {
                        try {
                          val x = 1
                        } catch {
                          case foo : Exception =>
                            throw foo
                        }
                    } """.stripMargin

      compileCodeSnippet(testCode)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "catch throwable immediately throw" - {
    "should report warning" in {

      val testCode = """object Test {
                        try {
                          val x = 1
                        } catch {
                          case foo : Throwable =>
                            throw foo
                        }
                    } """.stripMargin

      compileCodeSnippet(testCode)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }

  }

  "catch throwable and exception immediately throw" - {
    "should report all warnings" in {

      val testCode = """object Test {
                        try {
                          val x = 1
                        } catch {
                          case foo : Throwable =>
                            throw foo
                          case bar : Exception =>
                            throw bar
                        }
                    } """.stripMargin

      compileCodeSnippet(testCode)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
