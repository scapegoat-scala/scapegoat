package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class AvoidSizeEqualsZeroTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new AvoidSizeEqualsZero)

  "collection.size == 0" - {
    "should report warning" in {
      val code = """object Test {
                      val isEmpty1 = List(1).size == 0
                      val isEmpty2 = List(1).length == 0
                      val isEmpty3 = Set(1).size == 0
                      val isEmpty5 = Seq(1).size == 0
                      val isEmpty6 = Seq(1).length == 0
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 5
    }
    // github issue #94
    "should ignore durations" in {
      val code = """object Test {
                   |case class Duration(start: Long, stop: Long) {
                   |  def length: Long = stop - start
                   |  def isEmpty: Boolean = length == 0
                   |  }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
