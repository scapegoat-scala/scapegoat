package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class UnsafeContainsTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new UnsafeContains)

  "unsafe contains" - {
    "should report warning" in {
      val code = """object Test {
                      List(1).contains("sam")
                      val int = 1
                      List("sam").contains(int)
                      List(2).contains(int) // is good
                      List(new RuntimeException, new Exception).contains(new RuntimeException) // good
                      val name = "RuntimeException"
                      List(new RuntimeException).contains(name) //bad
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }
  }
}
