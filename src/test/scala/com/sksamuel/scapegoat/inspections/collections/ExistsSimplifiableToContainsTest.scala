package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class ExistsSimplifiableToContainsTest extends InspectionTest {

  override val inspections = Seq(new ExistsSimplifiableToContains)

  "should report warning" - {
    "when exists is called with compatible type" in {
      val code =
        """object Test {
          |val exists1 = List(1,2,3).exists(x => x == 2)
          |val list = List("sam", "spade")
          |val exists2 = list.exists(_ == "spoof")
          |val exists3 = (1 to 3).exists(_ == 2)
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 3
    }

    "when exists is called with a function mapping to something else" in {
      val code =
        """
          |object Test {
          |  def isItA(strings: String*): Boolean = {
          |    strings.exists { element =>
          |      element.toLowerCase == "a"
          |    }
          |  }
          |}
          |""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }

  "should not report warning" - {
    "when exists is called with incompatible type" in {
      val code =
        """object Test {
          |val exists1 = List("sam").exists(x => x == new RuntimeException)
          |val list = List("sam", "spade")
          |val exists2 = list.exists(_ == 3)
          |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "when exists is called on an Iterable" in {
      val code =
        """
          |object Test {
          | def method(): Unit = {
          |   val l: Iterable[String] = List[String]("a", "b", "c")
          |   print(l.exists(_ == "a"))
          | }
          |}""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "when exists is called with item comparing to a function of itself" in {
      val code =
        """
          |object Test {
          |  def atLeastOneIsAllLowercase(strings: String*): Boolean = {
          |    strings.exists { element =>
          |      element == element.toLowerCase
          |    }
          |  }
          |}
          |""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "when exists is called with a function transforming the elements in two different ways" in {
      val code =
        """
          |object Test {
          |  def containsNoA(strings: String*): Boolean = {
          |    strings.exists { element =>
          |      element.replaceAll("a", "").size == element.size
          |    }
          |  }
          |}
          |""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
