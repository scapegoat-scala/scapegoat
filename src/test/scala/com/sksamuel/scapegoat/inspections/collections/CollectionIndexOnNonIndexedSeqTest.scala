package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Josh Rosen */
class CollectionIndexOnNonIndexedSeqTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new CollectionIndexOnNonIndexedSeq)

  "collection index on non indexed Seq" - {
    "should report warning" in {
      val code = """object Test {
                      List(1,2,3)(1)
                      Seq(1,2,3)(2)
                      val s: Seq[Int] = Array(1,2,3)
                      s(2)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }
    "should not report warning" in {
      val code = """object Test {
                      Array(1,2,3)(1)
                      IndexedSeq(1,2,3)(2)
                      Vector(1,2,3)(2)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
