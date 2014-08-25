package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unsafe.IsInstanceOf
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class IsInstanceOfTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new IsInstanceOf)

  List(1).size

  "isInstanceOf" - {
    "should report warning" in {
      val code = """class Test {
                      def hello : Unit = {
                        val s : Any = "sammy"
                        println(s.isInstanceOf[String])
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
    "should ignore case classes synthetic methods" in {
      val code = """case class MappingCharFilter(name: String, mappings: (String, String)*)""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "should ignore partial functions" in {
      val code =
        """object Test {
          |val pf :PartialFunction[Any,Unit] = {
          |  case s : String => println(s)
          |  case i : Int if i == 4 => println(i)
          |  case _ => println("no match :(")
          |}
          |}
        """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "should ignore @SuppressWarnings when all is set" in {
      val code = """class Test {
                      @SuppressWarnings(Array("all"))
                      def hello : Unit = {
                        val s : Any = "sammy"
                        println(s.asInstanceOf[String])
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "should ignore @SuppressWarnings when this inspection is set" in {
      val code = """class Test {
                      @SuppressWarnings(Array("asinstanceof"))
                      def hello : Unit = {
                        val s : Any = "sammy"
                        println(s.asInstanceOf[String])
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
