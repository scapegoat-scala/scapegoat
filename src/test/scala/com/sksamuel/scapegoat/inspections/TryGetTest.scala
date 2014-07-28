package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class TryGetTest extends FreeSpec with ASTSugar with Matchers with PluginRunner {

  override val inspections = Seq(new TryGet)

  "try.get use" - {
    "should report warning" in {

      val code = """class Test {
                      import scala.util.Try
                      Try {
                        println("sam")
                      }.get
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 1
    }
  }
}
