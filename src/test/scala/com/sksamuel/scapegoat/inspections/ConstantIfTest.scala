package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class ConstantIfTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new ConstantIf)

  "ConstantIf" - {
    "should report warning" in {

      val code = """object Test {
                      if (1 < 2) {
                        println("sammy")
                      }
                      if (2 < 1) {
                        println("sammy")
                      }
                      if ("sam" == "sam".substring(0)) println("sammy")
                      if (true) println("sammy")
                      if (false) println("sammy")
                      if (1 < System.currentTimeMillis()) println("sammy")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 4
    }
  }
}
