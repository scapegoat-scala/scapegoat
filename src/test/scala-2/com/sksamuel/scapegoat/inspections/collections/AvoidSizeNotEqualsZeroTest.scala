package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class AvoidSizeNotEqualsZeroTest extends InspectionTest {

  override val inspections = Seq[Inspection](new AvoidSizeNotEqualsZero)

  "collection.size != 0" - {
    "should report warning" in {
      val code = """object Test {
                      val isEmpty1 = List(1).size != 0
                      val isEmpty2 = List(1).length != 0
                      val isEmpty3 = Set(1).size != 0
                      val isEmpty4 = Seq(1).size != 0
                      val isEmpty5 = Seq(1).length != 0
                      val isGreater1 = List(1).size > 0
                      val isGreater2 = List(1).length > 0
                      val isGreater3 = Set(1).size > 0
                      val isGreater4 = Seq(1).size > 0
                      val isGreater5 = Seq(1).length > 0
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 10
    }
    // github issue #414
    "should ignore durations" in {
      val code = """object Test {
                   |case class Duration(start: Long, stop: Long) {
                   |  def length: Long = stop - start
                   |  def nonEmpty: Boolean = length > 0
                   |  }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
