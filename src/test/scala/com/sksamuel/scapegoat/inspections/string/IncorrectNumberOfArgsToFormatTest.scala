package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class IncorrectNumberOfArgsToFormatTest extends InspectionTest {

  override val inspections = Seq(new IncorrectNumberOfArgsToFormat)

  "IncorrectNumberOfArgsToFormat" - {
    "should report warning" in {

      val code = """object Test {
                      "%s %.2f".format("sam")
                   } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
    "should not report warning" - {
      "for correct number of args" in {
        val code = """object Test {    "%s %.2f".format("sam", 4.5)  } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for correct number of args with array" in {
        val code = """object Test {  val arr = Array("sam");  "this is my [%s] string" format arr } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for correct number of args and various number of literal '%'s in format string" in {
        val code1 = """object Test {    "%%s".format()  } """
        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 0

        val code2 = """object Test {    "%%%s".format("sam")  } """
        compileCodeSnippet(code2)
        compiler.scapegoat.feedback.warnings.size shouldBe 0

        val code3 = """object Test {    "%%%%s".format()  } """
        compileCodeSnippet(code3)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for correct number of args when %n is used" in {
        val code1 = """object Test {    "Hello %s%n".format("World")  }  """
        compileCodeSnippet(code1)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
