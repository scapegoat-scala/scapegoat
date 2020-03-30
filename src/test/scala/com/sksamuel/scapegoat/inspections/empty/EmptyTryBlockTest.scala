package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class EmptyTryBlockTest extends InspectionTest {

  override val inspections = Seq(new EmptyTryBlock)

  "empty try block" - {
    "should report warning" in {

      val code = """object Test {

                   |        try {
                   |        } catch {
                   |          case r: RuntimeException => throw r
                   |          case e: Exception =>
                   |          case t: Throwable =>
                   |        }
                   |
                   |        try {
                   |          getClass
                   |        } catch {
                   |          case r: RuntimeException => throw r
                   |          case e: Exception =>
                   |          case t: Throwable =>
                   |        }

                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
