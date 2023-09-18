package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.inspections.unneccesary.RedundantFinalizer
import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class RedundantFinalizerTest extends InspectionTest {

  override val inspections: Seq[Inspection] = Seq(new RedundantFinalizer)

  "redundant finalizer" - {
    "should report warning" in {
      val code = """class Test {
                   |  override def finalize: Unit = {}
                   |  override def hashCode: Int = 3
                   |  def empty = {}
                   |}""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
