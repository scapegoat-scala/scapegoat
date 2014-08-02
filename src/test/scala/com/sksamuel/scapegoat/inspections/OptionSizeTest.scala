package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.option.OptionSize
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class OptionSizeTest extends FreeSpec with Matchers with PluginRunner {

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
