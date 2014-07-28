package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{Matchers, FreeSpec}

/** @author Stephen Samuel */
class FilterSizeToCountTest
  extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new FilterSizeToCount)

  "filter then size" - {
    "should report warning" in {

      val code = """object Test {
                      val list = List(1,2,3,4).filter(_ % 2 == 0).size
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
  }
}
