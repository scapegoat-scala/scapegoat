package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class AsInstanceOfTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new AsInstanceOf)

  "AsInstanceOf" - {
    "should report warning" in {
      val code = """class Test {
                      def hello : Unit = {
                        val s : Any = "sammy"
                        println(s.asInstanceOf[String])
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
    "should ignore case classes synthetic methods" in {
      val code = """case class MappingCharFilter(name: String, mappings: (String, String)*)""".stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 0
    }
  }
}
