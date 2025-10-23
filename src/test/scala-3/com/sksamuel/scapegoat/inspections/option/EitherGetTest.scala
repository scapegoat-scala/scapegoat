package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.{InspectionTest, Levels}

class EitherGetTest extends InspectionTest(classOf[EitherGet]) {

  "either.get use" - {
    "should report warning" in {

      val code = """class Test {
                   |  val l = Left("l")
                   |  l.left.get
                   |  val r = Right("r")
                   |  r.right.get
                    }""".stripMargin

      val feedback = runner.compileCodeSnippet(code)
      feedback.errors.assertable shouldEqual Seq(
        warning(2, Levels.Error, Some("l.left.get")),
        warning(4, Levels.Error, Some("r.right.get"))
      ).assertable
    }
  }

}
