package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class ReverseTakeReverseTest extends AnyFreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ReverseTakeReverse)

  "ReverseTakeReverse" - {
    "should report warning" in {
      val code = """class Test {
                     List(1,2,3).reverse.take(2).reverse
                     Array(1,2,3).reverse.take(1).reverse
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
