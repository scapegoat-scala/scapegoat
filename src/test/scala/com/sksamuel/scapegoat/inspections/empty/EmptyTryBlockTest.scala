package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class EmptyTryBlockTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new EmptyTryBlock)

  "empty try block" - {
    "should report warning" in {

      val code = """object Test {

                   |        try {
                   |        } catch {
                   |          case r: RuntimeException => throw r
                   |          case e: Exception =>
                   |          case t: Throwable =>
                   |        }
                   |
                   |        try {
                   |          getClass
                   |        } catch {
                   |          case r: RuntimeException => throw r
                   |          case e: Exception =>
                   |          case t: Throwable =>
                   |        }

                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
