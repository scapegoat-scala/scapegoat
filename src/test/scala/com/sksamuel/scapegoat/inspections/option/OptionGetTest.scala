package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class OptionGetTest extends AnyFreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new OptionGet)

  "option.get use" - {
    "should report warning" in {

      val code = """class Test {
                      val o = Option("sammy")
                      o.get
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
