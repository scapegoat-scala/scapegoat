package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.unsafe.TryGet

/** @author Stephen Samuel */
class TryGetTest extends InspectionTest {

  override val inspections = Seq(new TryGet)

  "try.get use" - {
    "should report warning" in {

      val code = """class Test {
                      import scala.util.Try
                      Try {
                        println("sam")
                      }.get
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
