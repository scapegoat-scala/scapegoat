package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class AvoidSizeNotEqualsZeroTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new AvoidSizeNotEqualsZero)

  "collection.size != 0" - {
    "should report warning" in {
      val code = """object Test {
                      val isEmpty1 = List(1).size != 0
                      val isEmpty2 = List(1).length != 0
                      val isEmpty3 = Set(1).size != 0
                      val isEmpty5 = Seq(1).size != 0
                      val isEmpty6 = Seq(1).length != 0
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 5
    }
  }
}
