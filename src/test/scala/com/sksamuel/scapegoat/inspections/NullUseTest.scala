package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unsafe.NullUse
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class NullUseTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new NullUse)

  "null use" - {
    "should report warning" - {
      "for null parameters to apply" in {
        val code = """object Test {
                        println(null)
                      } """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should have full snippet for method param" in {
      val code = """object Test {
                      println(null)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
      compiler.scapegoat.feedback.warnings.forall(_.snippet.get.contains("method argument"))
    }
    "should not report warning" - {
      "for override val in case class" in {
        val code = """abstract class Super(val name: String)
                    case class Boo(override val name: String) extends Super(name) """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for var args" in {
        val code = """class Birds(names:String*)"""
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for XML literals" in {
        addToClassPath("org.scala-lang.modules", "scala-xml_2.11", "1.0.2")
        val code = """object Test { val Dummy = <dummy/> } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for XML arguments" in {
        addToClassPath("org.scala-lang.modules", "scala-xml_2.11", "1.0.2")
        val code = """object Test { println(<dummy/>) } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}