package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class ImpossibleOptionSizeConditionTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new ImpossibleOptionSizeCondition)

  "options.size > x where x is > 1" - {
    "should report warning" in {

      val code = """class Test {
                      val o = Option("sammy")
                      val e = o.size > 2
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "options.size > x where x is <= 1" - {
    "should not report warning" in {

      val code = """class Test {
                      val o = Option("sammy")
                      val e = o.size > 1
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
