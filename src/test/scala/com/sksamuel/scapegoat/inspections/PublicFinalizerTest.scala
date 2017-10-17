package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class PublicFinalizerTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new PublicFinalizer)

  "public finalizer" - {
    "should report warning" - {
      "for a public overridden finalize method" in {
        val code = """class Test {
                        override def finalize : Unit = ()
                      }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for a protected overridden finalize method" in {
        val code = """class Test {
                        override protected def finalize : Unit = ()
                      }""".stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
