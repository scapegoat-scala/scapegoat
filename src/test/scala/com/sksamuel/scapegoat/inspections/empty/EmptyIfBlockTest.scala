package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class EmptyIfBlockTest extends AnyFreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new EmptyIfBlock)

  "empty if block" - {
    "should report warning" in {

      val code = """object Test {

                      if (true) {
                      }

                      if (true) {
                        ()
                      }

                      if (1 > 2) {
                        println("sammy")
                      }

                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
