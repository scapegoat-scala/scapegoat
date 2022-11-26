package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{isScala213, Inspection, InspectionTest}

/** @author Stephen Samuel */
class PredefIterableIsMutableTest extends InspectionTest {

  override val inspections: Seq[Inspection] = Seq(new PredefIterableIsMutable)

  "PredefIterableIsMutable" - {
    "should report warning" - {
      "for Iterable apply" in {
        val code = """object Test { val a = Iterable("sammy") }""".stripMargin
        val expectedWarnings = if (isScala213) 0 else 1
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe expectedWarnings
      }

      "for declaring Iterable as return type" in {
        val code = """object Test { def foo : Iterable[String] = ??? }""".stripMargin
        val expectedWarnings = if (isScala213) 0 else 1
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe expectedWarnings
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
