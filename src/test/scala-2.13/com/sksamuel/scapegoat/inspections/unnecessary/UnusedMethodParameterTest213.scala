package com.sksamuel.scapegoat.inspections.unnecessary

import com.sksamuel.scapegoat.inspections.unneccesary.UnusedMethodParameter
import com.sksamuel.scapegoat.{Inspection, InspectionTest}

class UnusedMethodParameterTest213 extends InspectionTest {

  override val inspections: Seq[Inspection] = Seq(new UnusedMethodParameter)

  "UnusedMethodParameter in Scala 2.13" - {
    "should not report warning" - {
      "for unused parameters if they are annotated with @unused (#340)" in {
        val code = """import scala.annotation.unused
                     |class Test {
                     |  def foo(@unused a:String, b: Int): Unit = {
                     |    println(b)
                     |  }
                     |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
        compiler.scapegoat.feedback.warns.size shouldBe 0
      }
    }
  }
}
