package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class RedundantFinalModifierOnVarTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new RedundantFinalModifierOnVar)

  "RedundantFinalModifierOnVar" - {
    "should report warning" - {
      "when object has final var" in {
        val code = """object Test {  final var foo = {}  } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when final class has final var" in {
        val code = """final class Test {  final var foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when case class has final var" in {
        val code = """case class Test() {  final var foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when case object has final var" in {
        val code = """case object Test {  final var foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when implicit object has final var" in {
        val code = """object A { implicit object B {  final var foo = {} } } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "when non final class has final var" in {
        val code = """class Test {  final var foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when trait has final var" in {
        val code = """trait Test {  final var foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when abstract class has final var" in {
        val code = """abstract class Test {  final var foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when implicit class has final var" in {
        val code = """object A { implicit class B(str:String) {  final var foo = {} } }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "on val fields" in {
        val code = """object A { val b = true }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "on non final var fields" in {
        val code = """object A { var b = true }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
