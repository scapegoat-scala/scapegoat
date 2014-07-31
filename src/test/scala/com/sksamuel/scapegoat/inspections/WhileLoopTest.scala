package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class WhileLoopTest extends FreeSpec with ASTSugar with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new WhileLoop)

  "while loop" - {
    "should report warning" in {

      val code = """import scala.collection.JavaConversions._
                    object Test {
                      while (true) {
                        println("sam")
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "do while loop" - {
    "should report warning" in {

      val code = """import scala.collection.JavaConversions._
                    object Test {
                      do {
                        println("sam")
                      } while (true)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
