package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.inspections.unsafe.AsInstanceOf
import com.sksamuel.scapegoat.{Inspection, InspectionTest}

class AsInstanceOfTest extends InspectionTest {

  override val inspections: Seq[Inspection] = Seq(new AsInstanceOf)

  "AsInstanceOf" - {
    "should report warning" in {
      val code = """class Test {
                   |  def hello: Unit = {
                   |    val s: Any = "sammy"
                   |    println(s.asInstanceOf[String])
                   |  }
                   |} """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }

    "should ignore case classes synthetic methods" in {
      val code = """case class MappingCharFilter(name: String, mappings: (String, String)*)""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should ignore partial functions" in {
      val code = """object Test {
                   |  val pf: PartialFunction[Any,Unit] = {
                   |    case s: String => println(s)
                   |    case i: Int if i == 4 => println(i)
                   |    case _ => println("no match :(")
                   |  }
                   |}""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should ignore @SuppressWarnings when all is set" in {
      val code = """class Test {
                   |  @SuppressWarnings(Array("all"))
                   |  def hello: Unit = {
                   |    val s: Any = "sammy"
                   |    println(s.asInstanceOf[String])
                   |  }
                   |}""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should ignore @SuppressWarnings when this inspection is set" in {
      val code = """class Test {
                   |  @SuppressWarnings(Array("asinstanceof"))
                   |  def hello: Unit = {
                   |    val s: Any = "sammy"
                   |    println(s.asInstanceOf[String])
                   |  }
                   |}""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should not warn on manifest of class" in {
      val code = """object Test {
                   |  @SuppressWarnings(Array("asinstanceof"))
                   |  val mf = manifest[Class[_]]
                   |}""".stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }

    "should not warn on GADT pattern matching (#378)" in {
      val code = """sealed trait MyGADT[T]
                   |final case class VariantInt(value: Int) extends MyGADT[Int]
                   |final case class VariantString(value: String) extends MyGADT[String]
                   |
                   |def doStuff[T](gadt: MyGADT[T]): T = {
                   |  gadt match {
                   |    case VariantInt(value)    => value
                   |    case VariantString(value) => value
                   |  }
                   |}""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
