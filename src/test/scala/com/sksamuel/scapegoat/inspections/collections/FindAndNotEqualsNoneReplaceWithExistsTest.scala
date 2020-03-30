package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class FindAndNotEqualsNoneReplaceWithExistsTest extends InspectionTest {

  override val inspections = Seq(new FindAndNotEqualsNoneReplaceWithExists)

  "FindAndNotEqualsNoneReplaceWithExists" - {
    "should report warning" - {
      "for find and not equals" in {
        val code = """object Test { List(1,2,3).find(_ > 0) != None } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for find" in {
        val code = """object Test { List(1,2,3).find(_ > 0) } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for != None" in {
        val code = """object Test { List(1,2,3) != None } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for find equals None" in {
        val code = """object Test { List(1,2,3).find(_ > 0) == None } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
