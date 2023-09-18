package com.sksamuel.scapegoat.inspections.unnecessary

import com.sksamuel.scapegoat.inspections.unneccesary.UnnecessaryReturnUse
import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class UnnecessaryReturnUseTest extends InspectionTest {

  override val inspections: Seq[Inspection] = Seq[Inspection](new UnnecessaryReturnUse)

  "return keyword use" - {
    "should report warning" in {
      val code = """class Test {
                   |  def hello : String = {
                   |    val s = "sammy"
                   |    return s
                   |  }
                   |  def earlyOutReturn: Unit = {
                   |    if(Math.random() > 0.5) {
                   |      println("early out return")
                   |      return () // Acceptable
                   |    }
                   |    println("not reachable")
                   |  }
                   |}""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
