package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class EmptyWhileBlockTest extends InspectionTest {

  override val inspections = Seq(new EmptyWhileBlock)

  "empty while block" - {
    "should report warning" in {

      val code =
        """object Test {
          |   while(true) {}
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "not empty while block" - {
    "should not report warning" in {

      val code =
        """object Test {
          |   while(true) { println("sam") }
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
