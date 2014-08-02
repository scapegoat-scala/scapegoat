package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.style.IncorrectlyNamedExceptions
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class IncorrectlyNamedExceptionsTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new IncorrectlyNamedExceptions)

  "incorrectly named exceptions" - {
    "should report warning" in {

      val code = """class NotException
                    class IsException extends Exception
                    class IsRuntimeException extends RuntimeException
                    class IsRuntime extends Exception
                    """.stripMargin

      compileCodeSnippet(code)
      // one for import 2 for ussage
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
