package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class ArraysToStringTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new ArraysToString)

  "array.toString" - {
    "should report warning" in {
      val code =
        """class Test {
             val array = Array(1,2,3,4)
             array.toString
             Array(5).toString
           } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
