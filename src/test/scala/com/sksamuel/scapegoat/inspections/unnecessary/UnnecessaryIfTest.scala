package com.sksamuel.scapegoat.inspections.unnecessary

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unneccesary.UnnecessaryIf
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class UnnecessaryIfTest
    extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new UnnecessaryIf)

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
    "should not report warning" - {
      "in empty case classes" in {
        val code =
          """object Test {
               case class DebuggerShutdownEvent()
             }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
