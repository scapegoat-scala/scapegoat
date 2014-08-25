package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Stephen Samuel */
class EmptyCatchBlockTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new EmptyCatchBlock)

  "empty catch block" - {
    "should report warning" in {

      val code = """object Test {
                   |        try {
                   |          val a = System.currentTimeMillis
                   |        } catch {
                   |          case r: RuntimeException => throw r
                   |          case e: Exception =>
                   |          case t: Throwable =>
                   |        }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
