package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

class ReverseTailReverseTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new ReverseTailReverse)

  "ReverseTailReverse" - {
    "should report warning" in {
      val code = """class Test {
                     List(1,2,3).reverse.tail.reverse
                     Array(1,2,3).reverse.tail.reverse
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
