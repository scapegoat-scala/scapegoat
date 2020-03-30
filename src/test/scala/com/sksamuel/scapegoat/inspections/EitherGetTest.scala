package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.option.EitherGet

class EitherGetTest extends InspectionTest {

  override val inspections = Seq(new EitherGet)

  "either right / left projection get use" - {
    "should report warning" in {
      val code = """class Test {
                   |      val l = Left("l")
                   |      l.left.get
                   |      val r = Right("r")
                   |      r.right.get
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
