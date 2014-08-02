package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class DuplicateMapKeyTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new DuplicateMapKey)

  import scala.reflect.runtime.universe

  val expr = universe.reify {
    Map("name" -> "sam")
  }
  println(universe showRaw expr.tree)

  "duplicate map keys" - {
    "should report warning" in {
      val code = """object Test {
                      Map("name" -> "sam", "location" -> "aylesbury", "name" -> "bob")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }


  "non duplicate map keys" - {
    "should not report warning" in {
      val code = """object Test {
                      Map("name" -> "sam", "location" -> "aylesbury", "name2" -> "bob")
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
