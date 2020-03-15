package com.sksamuel.scapegoat.inspections.names

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.naming.ObjectNames
import org.scalatest.OneInstancePerTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/** @author Stephen Samuel */
class ObjectNamesTest extends AnyFreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ObjectNames)

  "ObjectNames" - {
    "should report warning" - {
      "for objects containing underscore" in {
        val code =
          """object My_class
            |case object Your_class
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
    }
  }
}
