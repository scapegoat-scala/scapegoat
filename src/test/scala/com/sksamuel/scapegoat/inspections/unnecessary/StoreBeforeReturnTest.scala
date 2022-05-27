package com.sksamuel.scapegoat.inspections.unnecessary

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.unneccesary.StoreBeforeReturn

class StoreBeforeReturnTest extends InspectionTest {

  override val inspections = Seq(new StoreBeforeReturn)

  "store value before explicit return" - {
    "should report warning" in {
      val code =
        """
          |object Test {
          |  def hello(): String = {
          |    var s = "sammy"
          |    return s
          |  }
          |}
          |""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }

    "should report warning in a nested function" in {
      val code =
        """
          |object Test {
          |  def foo(): Int = {
          |    def bar(): Int = {
          |      val x = 1
          |      return x
          |    }
          |    return bar()
          |  }
          |}
          |""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "store value before implicit return" - {
    "should report warning" in {
      val code =
        """
          |object Test {
          |  def hello(): String = {
          |    var s = "sammy"
          |    s
          |  }
          |}
          |""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }

    "should report warning in a nested function" in {
      val code =
        """
          |object Test {
          |  def foo(): Int = {
          |    def bar(): Int = {
          |      val x = 1
          |      x
          |    }
          |    bar()
          |  }
          |}
          |""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "store value and modify before return" - {
    "should not report warning" in {
      val code =
        """
          |object Test {
          |  def hello(): Int = {
          |    var x = 1
          |    x = x + 1
          |    return  x
          |  }
          |}
          |""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }

  "store value and return for mutually-recusive functions" - {
    "should report a warning, and terminate" in {
      val code =
        """
          |object Test {
          |  def foo(): Int = {
          |    var x = bar()
          |    return  x
          |  }
          |  def bar(): Int = {
          |    val x = foo()
          |    return x
          |  }
          |}
          |""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }
}
