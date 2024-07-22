package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.inspections.option.EitherGet
import com.sksamuel.scapegoat.{Inspection, InspectionTest}

class EitherGetTest extends InspectionTest {

  override val inspections: Seq[Inspection] = Seq(new EitherGet)

  "either right / left projection get use" - {
    "should report warning" in {
      val code = """class Test {
                   |  val l = Left("l")
                   |  l.left.get
                   |  val r = Right("r")
                   |  r.right.get
                    }""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
