package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionTest}

/** @author Stephen Samuel */
class VariableShadowingTest extends InspectionTest {

  override val inspections = Seq[Inspection](new VariableShadowing)

  "VariableShadowing" - {
    "should report warning" - {
      "when variable shadows in nested def" in {
        val code =
          """class Test {
            |  def foo = {
            |    val a = 1
            |    def boo = {
            |      val a = 2
            |      println(a)
            |    }
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined in def shadows field" in {
        val code =
          """class Test {
            |  val a = 1
            |  def foo = {
            |    val a = 2
            |    println(a)
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined as case bind shadows field" in {
        val code =
          """class Test {
            |  val a = 1
            |  def foo(b : Int) = b match {
            |    case a => println(a)
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined in nested def in case shadows field" in {
        val code =
          """class Test {
            |  val a = 1
            |  def foo(b : Int) = b match {
            |    case f =>
            |     def boo() = {
            |       val a = 2
            |       println(a)
            |     }
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined in def shadows parameter" in {
        val code =
          """class Test {
            |  def foo(a : Int) = {
            |    val a = 1
            |    println(a)
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when variable defined in nested def shadows outer def parameter" in {
        val code =
          """class Test {
            |  def foo(a : Int) = {
            |    println(a)
            |    def boo = {
            |      val a = 2
            |      println(a)
            |    }
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "when sibling defs define same variable" in {
        val code =
          """class Test {
            |  def foo = {
            |    val a = 1
            |    println(a)
            |  }
            |  def boo = {
            |    val a = 2
            |    println(a)
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when two if branches define the same variable" in {
        val code =
          """class Test {
            |  if (1 > 0) {
            |    val something = 4
            |    println(something+1)
            |  } else {
            |    val something = 2
            |    println(something+2)
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when two sibling cases define the same local variable" in {
        val code =
          """class Test {
            |  val possibility: Option[Int] = Some(3) 
            |  possibility match {
            |    case Some(x) => 
            |      val y = x + 1    
            |      println(y)
            |    case None =>
            |      val y = 0
            |      println(y)     
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when visiting a match case, especially not visit it twice" in {
        val code =
          """class Test {
            |  val possibility: Option[Int] = Some(3)
            |  possibility match {
            |    case Some(x) =>
            |      val y = x + 1
            |      println(y)
            |    case None => println("None")
            |  }
            |}""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when sibling case classes use the same argument name" in {
        val code =
          """
            |final case class A(value: String)
            |final case class B(value: String)
            |final case class C(value: Int)
            |""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when the same variable is used in two sibling for loops (#342)" in {
        val code =
          """
            |object Test {
            |  for (i <- 1 to 10) println(i.toString)
            |  for (i <- 1 to 10) println(i.toString)
            |}
            |""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when using for-comprehension (#343)" in {
        val code =
          """
            |object Test {
            |  for {
            |    c <- "Hello, world!"
            |    if c != ','
            |  } println(c)
            |}
            |""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when using Builder-pattern method chaining (#398)" in {
        val code =
          """
            |object Test {
            |
            |  def f(x: Int): String = x.toString
            |  def g(y: String): Int = y.toInt
            |
            |  val a = Seq(1, 2, 3)
            |  System.out.println(
            |   a
            |    .map(s => f(s))
            |    .map(s => g(s))
            |  )
            |}
            |""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

      "when passing function as an argument (#419)" in {
        val code =
          """
            |class TestVariableShadowing {
            |  private def testCallbackFunction1(shadowedArg: Long): Boolean = shadowedArg > 10
            |  private def testCallbackFunction2(arg: Long): Boolean = arg < 10
            |
            |  private def testCaller(renamedArg: Long, func: Long => Boolean): Boolean = func(renamedArg)
            |
            |  def test(shadowedArg: AnyVal): Boolean =
            |    shadowedArg match {
            |      case l1: Long   => testCaller(l1, testCallbackFunction1)  // WARNING on testCallbackFunction1
            |      case l2: Double => testCaller(l2.toLong, testCallbackFunction2)
            |      case l3         => false
            |    }
            |}
            |""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }

    }
  }
}
