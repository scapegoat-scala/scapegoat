package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class EmptyIfBlockTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new EmptyIfBlock)

  "EmptyIfBlock" - {
    "should report warning" in {

      val code = """object Test {
                      try {
                        println("sammy")
                      } finally {
                      }

                      try {
                        println("sammy")
                      } finally {
                        println("snakes")
                      }

                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
  }
}
