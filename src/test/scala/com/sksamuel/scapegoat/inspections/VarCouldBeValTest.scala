package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unneccesary.VarCouldBeVal
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

class VarCouldBeValTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new VarCouldBeVal)

  "Var could be Val" - {
    "should report warning" - {
      "when a local var is not written to in the method" in {

        val code =
          """object Test {
            |  def foo {
            |    var count = 1
            |    println(count)
            |    for (k <- 1 until 10) {
            |      println(k + count)
            |    }
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when a local var is not written to in the method or nested methods" in {

        val code =
          """object Test {
            |  def foo {
            |    var count = 1
            |    println(count)
            |    def boo(n : Int) : Unit = {
            |      println(n + count)
            |    }
            |    for (k <- 1 until 10) {
            |      println(k + count)
            |    }
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }

      "with correct line numbers" in {
        val code =
          """object Test {
            |  def foo {
            |    var bar = 1
            |    val myValue = 2
            |    var baz = 3
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
        val warningsInOrder = compiler.scapegoat.feedback.warnings.sortBy(_.line)
        val Seq(barWarning, bazWarning) = warningsInOrder
        barWarning.line shouldBe 3
        barWarning.snippet should contain("bar is never written to, so could be a val: var bar: Int = 1")
        bazWarning.line shouldBe 5
        bazWarning.snippet should contain("baz is never written to, so could be a val: var baz: Int = 3")
      }
    }
    "should not report warning" - {
      "when var is written to in the method" in {

        val code =
          """object Test {
            |  def foo {
            |    var count = 1
            |    println(count)
            |    for (k <- 1 until 10) {
            |      count = count + 1
            |      println(k + count)
            |    }
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when var is written to in a nested method" in {

        val code =
          """object Test {
            |  def foo {
            |    var count = 1
            |    println(count)
            |    def boo(n : Int) : Unit = {
            |      count = n
            |      println( count)
            |    }
            |    for (k <- 1 until 10) {
            |      println(k + count)
            |    }
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when var is written to inside a finally block" in {
        val code =
          """object Test {
            |  def foo {
            |    var count = 1
            |    try {
            |      println("sam")
            |    } finally {
            |      count = 2
            |    }
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when var is written to inside an if else branch" in {
        val code =
          """object Test {
            |  def foo {
            |    var count = 1
            |    if (count == 10) {
            |      println("sam")
            |    } else {
            |      count = count + 1
            |    }
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when var is written to inside a match" in {
        val code =
          """object Test {
            |  def foo {
            |    var count = 1
            |    count match {
            |      case 10 => println("sam")
            |      case _ => count = count + 1
            |    }
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when var is written to inside an if " in {

        val code =
          """package com.sammy
            |object Test {
            |def test(b: Boolean): Int = {
            |  var count = 0
            |  if (b) count += 1
            |  count
            |}
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when var is written to inside a while condition " in {

        val code =
          """package com.sammy
            |object Test {
            |def something(): List[String] = List("a")
            |def test(): Unit = {
            |  var items = List.empty[String]
            |  while ({
            |   items = something()
            |   items.size
            |  } < 10) {
            |    println(items)
            |  }
            |}
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }


      "when var is written to for nested defs" in {
        val code =
          """
          |package com.sam
          |trait Iterator {
          |  def next : Int
          |}
          |object Test {
          |  val iterator = new Iterator {
          |    var last = -1
          |    def next: Int = {
          |      last = last + 1
          |      last
          |    }
          |  }
          |}
        """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
