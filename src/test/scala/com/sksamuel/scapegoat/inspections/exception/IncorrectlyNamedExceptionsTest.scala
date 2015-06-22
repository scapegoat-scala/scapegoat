package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class IncorrectlyNamedExceptionsTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

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
    "should not report warning" - {
      "for anon exceptions" in {

        val code =
          """object Test {
            |  val a = new RuntimeException("it went wrong") {  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for traits with Exception self-types" in {

        val code =
          """trait ErrorCodeException {
            |  this: Exception =>
            |
            |  def errorCode: Int
            |}
          """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
