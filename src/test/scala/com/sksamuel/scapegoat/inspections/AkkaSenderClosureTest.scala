package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class AkkaSenderClosureTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new VarClosure)

  "AkkaSenderClosure" - {
    "should report warning" - {
      "for closing over sender()" in {

        val code =
          """import scala.concurrent.Future
             class ActorTest extends Actor""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}
