package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class CollectionNamingConfusionTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new CollectionNamingConfusion)

  "collection confusing names" - {
    "should report warning" in {
      val code = """object Test {
                      val set = List(1)
                      val mySet = List(2)
                      val mySetWithStuff = List(3)
                      val list = Set(1)
                      val myList = Set(2)
                      val myListWithStuff = Set(3)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 6
    }
  }
}
