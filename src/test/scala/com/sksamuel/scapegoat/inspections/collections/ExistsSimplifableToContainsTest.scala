package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class ExistsSimplifableToContainsTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ExistsSimplifableToContains)

  "exists with compatible type" - {
    "should report warning" in {
      val code =
        """object Test {
          |val exists1 = List(1,2,3).exists(x => x == 2)
          |val list = List("sam", "spade")
          |val exists2 = list.exists(_ == "spoof")
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }

  "exists with incompatible type" - {
    "should not report warning" in {
      val code =
        """object Test {
          |val exists1 = List("sam").exists(x => x == new RuntimeException)
          |val list = List("sam", "spade")
          |val exists2 = list.exists(_ == 3)
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
