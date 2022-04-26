package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Bendix Saeltz */
class BooleanParameterTest extends InspectionTest {
  override val inspections: Seq[Inspection] = Seq(new BooleanParameter)

  "BooleanParameter" - {
    "should report info" - {
      "for methods using Boolean parameter" in {
        val code = """class Test {
                       def foo(bool: Boolean) = 4
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}
