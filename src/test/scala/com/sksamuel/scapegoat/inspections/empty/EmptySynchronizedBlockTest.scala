package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class EmptySynchronizedBlockTest extends InspectionTest {

  override val inspections = Seq(new EmptySynchronizedBlock)

  "empty empty" - {
    "should report warning" in {

      val code = """class Test {
                   |  synchronized {
                   |    println("sammy")
                   |  }
                   |  val a = {
                   |    println("sammy")
                   |  }
                   |  synchronized {
                   |  }
                   |  val b = {
                   |  }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
