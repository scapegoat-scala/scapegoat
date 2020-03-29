package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class CollectionNamingConfusionTest extends InspectionTest {

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
    "should not report warning" in {
      val code = """object Test {
                      val currentLBListenerSettings = List(1)
                      val exclusionsForAdvancedSetup = List(2, 3)
                      val preSetupSteps = List(4)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
