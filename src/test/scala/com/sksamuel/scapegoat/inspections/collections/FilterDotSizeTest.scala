package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Stephen Samuel */
class FilterDotSizeTest
    extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new FilterDotSize)

  "filter then size" - {
    "should report warning" in {

      val code = """object Test {
                      val list = List(1,2,3,4).filter(_ % 2 == 0).size
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
