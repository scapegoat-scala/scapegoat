package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.InspectionTest
import com.sksamuel.scapegoat.inspections.unsafe.FinalizerWithoutSuper

/** @author Stephen Samuel */
class FinalizerWithoutSuperTest extends InspectionTest {

  override val inspections = Seq(new FinalizerWithoutSuper)

  "FinalizerWithoutSuper" - {
    "should report warning" - {
      "when empty finalizer" in {
        val code =
          """class Test {
            |  override def finalize {
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
      "when non finalizer" in {
        val code =
          """class Test {
            |  override def finalize {
            |    println("sam")
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
  }
  "FinalizerWithoutSuper" - {
    "should not report warning" - {
      "when def contains super.finalize() as first call" in {
        val code =
          """class Test {
            |  override def finalize {
            |    super.finalize()
            |    println("sam")
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when def contains super.finalize() as intermediate call" in {
        val code =
          """class Test {
            |  override def finalize {
            |    println("sam")
            |    super.finalize()
            |    println("sam")
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "when def contains super.finalize() as final call" in {
        val code =
          """class Test {
            |  override def finalize {
            |    println("sam")
            |    super.finalize()
            |  }
            |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
