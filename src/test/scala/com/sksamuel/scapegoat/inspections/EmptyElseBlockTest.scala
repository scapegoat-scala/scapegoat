package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class EmptyElseBlockTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new EmptyElseBlock)

  "EmptyElseBlock" - {
    "should report warning" in {

      val code = """object Test {
                      if (true) {
                        println("sammy")
                      } else {
                      }

                      if (true) {
                        println("sammy")
                      } else {
                        println("snakes")
                      }

                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
  }
}
