package com.sksamuel.scapegoat.inspections.imports

import com.sksamuel.scapegoat.InspectionTest

/** @author Stephen Samuel */
class WildcardImportTest extends InspectionTest {

  override val inspections = Seq(new WildcardImport)

  "WildcardImport" - {
    "should report warning" - {
      "for wildcard imports" in {

        val code =
          """import scala.concurrent._
             object Test { }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
}
