package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class OptionGetTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(OptionGet)

  "option.get use" - {
    "should report warning" in {

      val code = """class Test {
                      val o = Option("sammy")
                      o.get
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
  }
}
