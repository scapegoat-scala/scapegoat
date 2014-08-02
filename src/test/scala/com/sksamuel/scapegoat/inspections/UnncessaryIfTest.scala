package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class UnncessaryIfTest
  extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new UnncessaryIf)

  "unncessary if" - {
    "should report warning" in {

      val code = """object Test {
                      val a = "sam"
                      if (a == "sam") true else false
                      if (a == "sam") false else true
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
