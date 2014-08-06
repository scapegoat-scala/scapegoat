package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class ParameterlessMethodReturnsUnitTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new ParameterlessMethodReturnsUnit)

  "ParameterlessMethodReturnsUnit" - {
    "should report warning" in {

      val code = """object Test {
                   | def paramless: Unit = ()
                   | def paramless2 : Int = 4
                   | def params(): Unit = ()
                   | def params2() : Int = 4
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
