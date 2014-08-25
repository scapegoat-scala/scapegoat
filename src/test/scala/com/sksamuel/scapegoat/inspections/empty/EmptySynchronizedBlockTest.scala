package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Stephen Samuel */
class EmptySynchronizedBlockTest extends FreeSpec with Matchers with PluginRunner {

  override val inspections = Seq(new EmptySynchronizedBlock)

  "empty empty" - {
    "should report warning" in {

      val code = """class Test {
                   |  synchronized {
                   |    println("sammy")
                   |  }
                   |  val a = {
                   |    println("sammy")
                   |  }
                   |  synchronized {
                   |  }
                   |  val b = {
                   |  }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
