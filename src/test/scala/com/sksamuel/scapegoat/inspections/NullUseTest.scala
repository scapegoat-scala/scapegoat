package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unsafe.NullUse
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class NullUseTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new NullUse)

  "null use" - {
    "should report warning" in {

      val code = """object Test {
                      println(null)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
    "should have full snippet for method param" in {
      val code = """object Test {
                      println(null)
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
      compiler.scapegoat.feedback.warnings.forall(_.snippet.get.contains("method argument"))
    }
    "should handle override val in case class" in {
      val code = """abstract class Super(val name: String)
                    case class Boo(override val name: String) extends Super(name) """
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
    "should handle var args" in {
      val code = """class Birds(names:String*)"""
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}