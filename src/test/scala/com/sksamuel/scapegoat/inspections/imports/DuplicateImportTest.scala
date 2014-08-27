package com.sksamuel.scapegoat.inspections.imports

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class DuplicateImportTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new DuplicateImport)

  "DuplicatedImport" - {
    "should report warning" - {
      "for duplicated top level imports" in {

        val code =
          """import scala.concurrent.duration.TimeUnit
             import scala.concurrent.duration.TimeUnit
             object Test { }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "for duplicated top level multiple selector imports" in {

        val code =
          """import scala.collection.immutable.Set
             import scala.collection.immutable.{Set, Seq}
             object Test { }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      //      "for duplicated nested imports" in {
      //
      //        val code =
      //          """import scala.collection.immutable.Set
      //             object Test {
      //              def foo = {
      //                import scala.collection.immutable.Set
      //                Set.empty
      //              }
      //             }""".stripMargin
      //
      //        compileCodeSnippet(code)
      //        compiler.scapegoat.feedback.warnings.size shouldBe 1
      //      }
    }
    "should not report warning" - {
      "for sibling class imports" in {
        val code =
          """
             object A {
              import scala.collection.immutable.Set
             }
             class B {
               import scala.collection.immutable.Set
             } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
