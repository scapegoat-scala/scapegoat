package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class ReturnUseTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new ReturnUse)

  "return keyword use" - {
    "should report warning" in {

      val code = """class Test {
                      def hello : String = {
                        val s = "sammy"
                        return s
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
  }
}
