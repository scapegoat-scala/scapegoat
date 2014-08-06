package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unneccesary.IntToInt
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class IntToIntTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new IntToInt)

  "incorrectly named exceptions" - {
    "should report warning" in {

      val code = """object Test {
                      val i = 4
                      val j = i.toInt

                      val s = "5"
                      val t = s.toInt
                    }
                    """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
