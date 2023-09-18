package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class OptionSizeTest extends InspectionTest {

  override val inspections = Seq[Inspection](new OptionSize)

  "option.size use" - {
    "should report warning" in {

      val code = """class Test {
                      Option("sammy").size
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
