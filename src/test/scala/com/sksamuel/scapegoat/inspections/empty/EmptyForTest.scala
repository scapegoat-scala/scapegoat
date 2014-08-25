package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class EmptyForTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new EmptyFor)

  "EmptyFor" - {
    "should report warning" in {

      val code = """object Test {
                     for (k <- 1 to 100) {
                     }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
    "should not report warning" - {
      "for non empty loop" in {
        val code = """object Test {
                     for (k <- 1 to 100) {
                       println("sam")
                     }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
