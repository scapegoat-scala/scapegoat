package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class RedundantFinalModifierOnMethodTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new RedundantFinalModifierOnMethod)

  "RedundantFinalModifierOnMethod" - {
    "should report warning" - {
      "when object has final method" in {
        val code = """object Test {  final def foo = {}  } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when final class has final method" in {
        val code = """final class Test {  final def foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when case class has final method" in {
        val code = """case class Test() {  final def foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when case object has final method" in {
        val code = """case object Test {  final def foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when implicit object has final method" in {
        val code = """object A { implicit object B {  final def foo = {} } } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for value classes" in {
        val code =
          """class A(val str:String) extends AnyVal { final def foo = str }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "when non final class has final method" in {
        val code = """class Test {  final def foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when trait has final method" in {
        val code = """trait Test {  final def foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when abstract class has final method" in {
        val code = """abstract class Test {  final def foo = {} } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when implicit class has final method" in {
        val code = """object A { implicit class B(str:String) {  final def foo = {} } }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "on val fields" in {
        val code = """object A { final val b = true }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "on var fields" in {
        val code = """object A { final var b = true }"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for partial functions" in {
        val code =
          """
            |object Resolved {
            |  import java.net.{InetAddress, Inet4Address, Inet6Address}
            |  def apply(name: String, addresses: Iterable[InetAddress]): Seq[InetAddress] = {
            |    val ipv4: Seq[Inet4Address] = addresses.collect({ case a: Inet4Address ⇒ a}).toSeq
            |    val ipv6: Seq[Inet6Address] = addresses.collect({ case a: Inet6Address ⇒ a}).toSeq
            |    ipv4 ++ ipv6
            |  }
            |}
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}

