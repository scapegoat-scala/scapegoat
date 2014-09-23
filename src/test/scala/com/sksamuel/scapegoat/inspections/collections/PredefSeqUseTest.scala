package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class PredefSeqUseTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new PredefSeqUse)

  "PredefSeqUse" - {
    "should report warning" - {
      "for predef seq usage" in {
        val code = """object Test { val a = Set("sammy") }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for scala.collection.mutable usage" in {
        val code =
          """import scala.collection.mutable.Set
            |object Test { val a = Set("sammy") }""".stripMargin
      }
      "for scala.collection.immutable usage" in {
        val code =
          """import scala.collection.immutable.Set
            |object Test { val a = Set("sammy") }""".stripMargin
      }
    }
  }
}
