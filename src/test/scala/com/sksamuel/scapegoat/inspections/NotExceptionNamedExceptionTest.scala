package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class NotExceptionNamedExceptionTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new NotExceptionNamedException)

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
