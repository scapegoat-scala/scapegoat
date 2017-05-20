package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Josh Rosen */
class CollectionIndexOnNonIndexedSeqTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new CollectionIndexOnNonIndexedSeq)

  "collection index on non indexed seq" - {
    "should report warning" in {
      val code =
        """
          | object Test {
          |   val idx = 2
          |   List(1,2,3)(idx)
          |   Seq(1,2,3)(idx)
          |   val s: Seq[Int] = Array(1,2,3)
          |   s(idx)
          | }
        """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }
    "should not report warning" - {
      "for literal index" in {
        val code =
          """
            | object Test {
            |   List(1,2,3)(1)
            |   Seq(1,2,3)(2)
            | }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for IndexedSeq" in {
        val code =
          """
            | object Test {
            |   val idx = 2
            |   IndexedSeq(1,2,3)(idx)
            |   Array(1,2,3)(idx)
            |   Vector(1,2,3)(idx)
            | }
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
