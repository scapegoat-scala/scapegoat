package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class OptionSizeTest extends AnyFreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new OptionSize)

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
