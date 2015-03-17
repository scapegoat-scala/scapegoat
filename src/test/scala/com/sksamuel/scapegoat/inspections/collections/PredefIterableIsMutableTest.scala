package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class PredefIterableIsMutableTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new PredefIterableIsMutable)

  "PredefIterableIsMutable" - {
    "should report warning" - {
      "for Iterable apply" in {
        val code = """object Test { val a = Iterable("sammy") }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for declaring Iterable as return type" in {
        val code = """object Test { def foo : Iterable[String] = ??? }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for scala.collection.mutable usage" in {
        val code = """import scala.collection.mutable.Iterable
                     |object Test { val a = Iterable("sammy") }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.immutable usage" in {
        val code = """import scala.collection.immutable.Iterable
                     |object Test { val a = Iterable("sammy") }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.mutable defs" in {
        val code = """import scala.collection.mutable.Iterable
                     |object Test { def foo : Iterable[String] = ??? }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.immutable defs" in {
        val code = """import scala.collection.immutable.Iterable
                     |object Test { def foo : Iterable[String] = ??? }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
