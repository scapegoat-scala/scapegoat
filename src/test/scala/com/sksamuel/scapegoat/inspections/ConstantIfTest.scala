package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.unneccesary.ConstantIf

import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class ConstantIfTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ConstantIf)

  "ConstantIf" - {
    "should report warning" in {
      val code = """object Test {
                      if (1 < 2) {
                        println("sammy")
                      }
                      if (2 < 1) {
                        println("sammy")
                      }
                      if ("sam" == "sam".substring(0)) println("sammy")
                      if (true) println("sammy")
                      if (false) println("sammy")
                      if (1 < System.currentTimeMillis()) println("sammy")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 4
    }
    "should not report warning" - {
      "for while loops" in {
        val code = """object Test { while ( true ) { println("sam") } } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for subclasses of TypeCreator" in {
        val code =
          """import scala.reflect.api.{Mirror, Universe, TypeCreator}
            |class Test extends TypeCreator {
            |  override def apply[U <: Universe with Singleton](m: Mirror[U]): U#Type = {
            |    if (1 < 2)
            |      throw new RuntimeException
            |    else
            |      null
            |  }
            |}
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}

