package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.InspectionTest

class UnsafeTraversableMethodsTest extends InspectionTest {

  override val inspections = Seq(new UnsafeTraversableMethods)

  val unsafeMethodUsages = Set(
    "head",
    "tail",
    "init",
    "last",
    "reduce(_+_)",
    "reduceLeft(_+_)",
    "reduceRight(_+_)",
    "max",
    "maxBy(x => x)",
    "min",
    "minBy(x => x)"
  )

  unsafeMethodUsages.foreach { unsafeMethodUsage =>
    s"Traversable.$unsafeMethodUsage use" - {
      "should report warning" in {

        val code = s"""class Test {
                      Seq(1).$unsafeMethodUsage
                      List(1).$unsafeMethodUsage
                      Vector(1).$unsafeMethodUsage
                      Iterable(1).$unsafeMethodUsage
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 4
      }

      "should not report warning" - {
        "for var args" in {
          val code = """class F(args:String*)""".stripMargin
          compileCodeSnippet(code)
          compiler.scapegoat.feedback.warnings.size shouldBe 0
        }
      }
    }

  }

  "Regression" - {
    "should report warning in method chains" in {
      val code = "class Test { List((1, 2)).head._1 }"

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
}
