package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.InspectionTest

/** @author Zack Grannan */
class UnsafeStringContainsTest extends InspectionTest {
  override val inspections = Seq(new UnsafeStringContains)
  "unsafe string contains" - {
    "should report warning" in {
      val code =
        """
          |object Test {
          |  "abcdefgh".contains(2) // bad
          |  "abcdefgh".contains(2:Char) // good
          |  "abcdefgh".contains(new Object) // bad
          |  val str: String = ""
          |  str.contains(2L) // bad
          |  "abcd".contains("abc") // good
          |  "abcd".contains('b') // good
          |  str.contains("abc") // good
          |  str.contains('b') // good
          |  Seq("one", "two", "three").forall("abcdefgh".contains) // good
          |}""".stripMargin.trim
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }
  }

}
