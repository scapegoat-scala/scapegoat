package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class IterableHeadTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new IterableHead)

  "option.head use" - {
    "should report warning" in {

      val code = """class Test {
                      val o = Option("sammy")
                      o.head
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
