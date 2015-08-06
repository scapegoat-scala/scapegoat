package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.test.ScapegoatTestPluginRunner
import org.scalatest.{ FreeSpec, Matchers }

/** @author Stephen Samuel */
class FilterOptionAndGetTest
    extends FreeSpec with Matchers with ScapegoatTestPluginRunner {

  override val inspections = Seq(new FilterOptionAndGet)

  "filter then size" - {
    "should report warning" in {

      val code = """object Test {
                      val a = List(None, Some("sam")).filter(_.isDefined).map(_.get)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
