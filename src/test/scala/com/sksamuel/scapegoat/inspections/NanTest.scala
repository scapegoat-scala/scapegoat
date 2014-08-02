package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.equality.ComparingFloatingPointTypes
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class NanTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new ComparingFloatingPointTypes)

  "self assignment" - {
    "should report warning" - {
      val code = """class Test {
                      def compareFloats : Boolean = {
                        val a = 14.5f
                        val b = 15.6f
                        a == b
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
