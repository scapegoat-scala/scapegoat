package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class ExistsSimplifiableToContainsTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ExistsSimplifiableToContains)

  "exists with compatible type" - {
    "should report warning" in {
      val code =
        """object Test {
          |val exists1 = List(1,2,3).exists(x => x == 2)
          |val list = List("sam", "spade")
          |val exists2 = list.exists(_ == "spoof")
          |val exists3 = (1 to 3).exists(_ == 2)
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
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
