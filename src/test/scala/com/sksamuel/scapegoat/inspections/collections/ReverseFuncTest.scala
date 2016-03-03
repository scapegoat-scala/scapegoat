package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

class ReverseFuncTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new ReverseFunc)

  "ReverseFunc" - {
    "should report warning" in {
      val code = """class Test {
                     List(1,2,3).reverse.head
                     List(1,2,3).reverse.headOption
                     List(1,2,3).reverse.iterator
                     List(1,2,3).reverse.map(identity)
                     Array(1,2,3).reverse.head
                     Array(1,2,3).reverse.headOption
                     Array(1,2,3).reverse.iterator
                     Array(1,2,3).reverse.map(identity)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 8
    }
  }
}
