package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class FilterDotIsEmptyTest
    extends AnyFreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new FilterDotIsEmpty)

  "self assignment" - {
    "should report warning" in {
      val code = """class Test {
                     val empty = List(1,2,3).filter(_ < 0).isEmpty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
