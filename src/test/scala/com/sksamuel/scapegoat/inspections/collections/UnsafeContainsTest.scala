package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class UnsafeContainsTest extends InspectionTest {

  override val inspections = Seq(new UnsafeContains)

  "unsafe contains" - {
    "should report warning" in {
      val code =
        """
          |object Test {
          |  import scala.language.higherKinds
          |  def f1[A](xs: Seq[A], y: A)                                        = xs contains y  // good
          |  def f2[A <: AnyRef](xs: Seq[A], y: Int)                            = xs contains y  // bad
          |  def f3[A <: AnyRef](xs: Vector[A], y: Int)                         = xs contains y  // bad
          |  def f4[CC[X] <: Seq[X], A](xs: CC[A], y: A)                        = xs contains y  // good
          |  def f5[CC[X] <: Seq[X], A <: AnyRef, B <: AnyVal](xs: CC[A], y: B) = xs contains y  // bad
          |
          |  List(1).contains("sam")
          |  Some(1).contains("sam")
          |  val int = 1
          |  List("sam").contains(int)
          |  List(2).contains(int) // is good
          |  List(new RuntimeException, new Exception).contains(new RuntimeException) // good
          |  val name = "RuntimeException"
          |  List(new RuntimeException).contains(name) // bad
          |}""".stripMargin.trim

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 7
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
      "for Seq filtering with Seq contains" in {
        val code = """
                     |package com.sam
                     |object Test {
                     |val words = Seq("Hello", "world")
                     |val moreWords = Seq("Goodbye", "cruel", "world")
                     |val common = moreWords.filter(words.contains)
                     |}
                     | """.stripMargin.trim

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for Seq filtering by lambda with Seq contains" in {
        val code = """
                     |package com.sam
                     |object Test {
                     |val words = Seq("Hello", "world")
                     |val moreWords = Seq("Goodbye", "cruel", "world")
                     |val common = moreWords.filter(v => words.contains(v))
                     |}
                     | """.stripMargin.trim

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for exists check with named function argument" in {
        val code = """
                     |package com.sam
                     |object Test {
                     |def distinctIndices(a: Seq[Int], b: Seq[Int]): Boolean = !a.exists(b.contains)
                     |}
                     |""".stripMargin.trim

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for exists check with lambda argument" in {
        val code = """
                     |package com.sam
                     |object Test {
                     |def distinctIndices(a: Seq[Int], b: Seq[Int]): Boolean = !a.exists(v => b.contains(v))
                     |}
                     |""".stripMargin.trim

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
