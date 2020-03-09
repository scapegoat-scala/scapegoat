package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class DuplicateSetValueTest extends AnyFreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new DuplicateSetValue)

  "duplicate set literals" - {
    "should report warning" in {
      val code = """object Test {
                      Set("sam", "aylesbury", "sam")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "non duplicate set literals" - {
    "should not report warning" in {
      val code = """object Test {
                      Set("name", "location", "aylesbury", "bob")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }

  "duplicate etas" - {
    "should not report warning" in {
      val code = """object Test {
                      def name = "could be random"
                      Set(name, "middle", name)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
