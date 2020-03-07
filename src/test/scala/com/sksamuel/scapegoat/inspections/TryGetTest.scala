package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unsafe.TryGet
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class TryGetTest extends AnyFreeSpec with PluginRunner with Matchers {

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
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
