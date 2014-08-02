package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class FilterHeadOptionTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new FilterHeadOption)

  "self assignment" - {
    "should report warning" - {
      val code = """class Test {
                     List(1,2,3).filter(_ < 0).headOption
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
