package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class EmptyIfBlockTest extends InspectionTest {

  override val inspections = Seq(new EmptyIfBlock)

  "empty if block" - {
    "should report warning" in {

      val code =
        """object Test {

                      if (true) {
                      }

                      if (true) {
                        ()
                      }

                      if (1 > 2) {
                        println("sammy")
                      }

                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }

    "should not report warning" - {
      "the if-block is the return value" in {
        val code =
          """
            |import scala.concurrent.Future
            |import scala.concurrent.ExecutionContext.Implicits.global
            |object Test {
            |  Future("test").map(value => {
            |   if (value.contains("foo")) {
            |     ()
            |   } else {
            |     throw new IllegalStateException("Bar!")
            |   }
            |  })
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
