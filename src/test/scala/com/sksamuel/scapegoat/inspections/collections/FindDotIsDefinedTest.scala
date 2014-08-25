package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Stephen Samuel */
class FindDotIsDefinedTest
    extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new FindDotIsDefined)

  "filter then size" - {
    "should report warning" in {

      val code = """object Test {
                      val a = List(1,2,3).find(_>4).isDefined
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
