package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.unneccesary.VarCouldBeVal

class VarCouldBeValTest extends InspectionTest {

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
        val warningsInOrder = compiler.scapegoat.feedback.warnings.sortBy(_.line)

        warningsInOrder.size shouldBe 2
        warningsInOrder match {
          case Seq(barWarning, bazWarning) =>
            barWarning.line shouldBe 3
            barWarning.snippet.exists(_.contains("var bar: Int = 1")) shouldBe true
            bazWarning.line shouldBe 5
            bazWarning.snippet.exists(_.contains("var baz: Int = 3")) shouldBe true
        }
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
