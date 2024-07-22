package com.sksamuel.scapegoat.inspections.names

import com.sksamuel.scapegoat.inspections.naming.ObjectNames
import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class ObjectNamesTest extends InspectionTest {

  override val inspections: Seq[Inspection] = Seq(new ObjectNames)

  "ObjectNames" - {
    "should report warning" - {
      "for objects containing underscore" in {
        val code = """object My_class
                     |case object Your_class""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
    }
  }
}
