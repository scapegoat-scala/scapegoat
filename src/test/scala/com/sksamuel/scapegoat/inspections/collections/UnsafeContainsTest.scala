package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class UnsafeContainsTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new UnsafeContains)

  "unsafe contains" - {
    "should report warning" in {
      val code = """
      |object Test {
      |  import scala.language.higherKinds
      |  def f1[A](xs: Seq[A], y: A)                                        = xs contains y  // good
      |  def f2[A <: AnyRef](xs: Seq[A], y: Int)                            = xs contains y  // bad
      |  def f3[A <: AnyRef](xs: Vector[A], y: Int)                         = xs contains y  // bad
      |  def f4[CC[X] <: Seq[X], A](xs: CC[A], y: A)                        = xs contains y  // good
      |  def f5[CC[X] <: Seq[X], A <: AnyRef, B <: AnyVal](xs: CC[A], y: B) = xs contains y  // bad
      |
      |  List(1).contains("sam")
      |  val int = 1
      |  List("sam").contains(int)
      |  List(2).contains(int) // is good
      |  List(new RuntimeException, new Exception).contains(new RuntimeException) // good
      |  val name = "RuntimeException"
      |  List(new RuntimeException).contains(name) // bad
      |}""".stripMargin.trim

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 6
    }
    "should not report warning" - {
      "for type parameter A in method, collection, and value" in {
        val code = """
                      package com.sam
                     |class C {
                     |  def f[A](xs: Seq[A], y: A) = xs contains y
                     |}
                     | """.stripMargin.trim

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0

      }
    }
  }
}
