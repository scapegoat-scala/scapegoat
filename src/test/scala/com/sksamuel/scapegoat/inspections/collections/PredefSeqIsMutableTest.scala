package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{isScala213, InspectionTest}

/** @author Stephen Samuel */
class PredefSeqIsMutableTest extends InspectionTest {

  override val inspections = Seq(new PredefSeqIsMutable)

  "PredefSeqUse" - {
    "should report warning (for Scala < 2.13)" - {
      "for predef seq apply" in {
        val code = """object Test { val a = Seq("sammy") }""".stripMargin
        val expectedWarnings = if (isScala213) 0 else 1
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings should have size expectedWarnings
      }
      "for declaring Seq as return type" in {
        val code = """object Test { def foo : Seq[String] = ??? }""".stripMargin
        val expectedWarnings = if (isScala213) 0 else 1
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings should have size expectedWarnings
      }
    }
    "should not report warning" - {
      "for scala.collection.mutable usage" in {
        val code = """import scala.collection.mutable.Seq
                     |object Test { val a = Seq("sammy") }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.immutable usage" in {
        val code = """import scala.collection.immutable.Seq
                     |object Test { val a = Seq("sammy") }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.mutable defs" in {
        val code = """import scala.collection.mutable.Seq
                     |object Test { def foo : Seq[String] = ??? }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for scala.collection.immutable defs" in {
        val code = """import scala.collection.immutable.Seq
                     |object Test { def foo : Seq[String] = ??? }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
