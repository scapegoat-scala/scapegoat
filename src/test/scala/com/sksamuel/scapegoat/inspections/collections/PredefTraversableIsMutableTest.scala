package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{isScala213, InspectionTest}

/** @author Stephen Samuel */
class PredefTraversableIsMutableTest extends InspectionTest {

  override val inspections = Seq(new PredefTraversableIsMutable)

  "PredefTraversableIsMutable" - {
    "should report warning (for Scala < 2.13)" - {
      "for predef Traversable apply" in {
        val code = """object Test { val a = Traversable("sammy") }""".stripMargin
        val expectedWarnings = if (isScala213) 0 else 1
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings should have size expectedWarnings
      }
      "for declaring Traversable as return type" in {
        val code = """object Test { def foo : Traversable[String] = ??? }""".stripMargin
        val expectedWarnings = if (isScala213) 0 else 1
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings should have size expectedWarnings
      }
    }
    "should not report warning" - {
      "for scala.collection.mutable usage" in {
        val code = """import scala.collection.mutable.Traversable
                     |object Test { val a = Traversable("sammy") }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.immutable usage" in {
        val code = """import scala.collection.immutable.Traversable
                     |object Test { val a = Traversable("sammy") }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.mutable defs" in {
        val code = """import scala.collection.mutable.Traversable
                     |object Test { def foo : Traversable[String] = ??? }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.immutable defs" in {
        val code = """import scala.collection.immutable.Traversable
                     |object Test { def foo : Traversable[String] = ??? }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
