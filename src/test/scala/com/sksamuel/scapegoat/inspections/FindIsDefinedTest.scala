package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.collections.FindIsDefined
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class FindIsDefinedTest
  extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new FindIsDefined)

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
