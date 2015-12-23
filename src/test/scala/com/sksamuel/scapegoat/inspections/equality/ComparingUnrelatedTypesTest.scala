package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class ComparingUnrelatedTypesTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ComparingUnrelatedTypes)

  "ComparingUnrelatedTypes" - {
    "should report warning" in {
      val code = """
        case class SomeValueClass(i: Int) extends AnyVal
        class Test {
          val a = new RuntimeException
          val b = new Exception

          "sammy" == BigDecimal(10) // warning 1
          "sammy" == "bobby" // same type
          a == b // superclass
          a == "sammy" // warning 2
          Some("sam") == Option("laura") // ok
          Nil == Set.empty // warning 3
          Some(1) match {
            case Some(x) if x == SomeValueClass(1) => () // warning 4
            case _ => ()
          }
        } """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 4
    }
    "should not report warning" - {
      "for long compared to zero" in {
        val code = """object A { val l = 100l; val b = l == 0 } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for double compared to zero" in {
        val code = """object A { val d = 100d; val b = d == 0 } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for float compared to zero" in {
        val code = """object A { val f = 100f; val b = f == 0 } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for zero compared to long" in {
        val code = """object A { val l = 100l; val b = 0 == l } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for zero compared to double" in {
        val code = """object A { val d = 100d; val b = 0 == d } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for zero compared to float" in {
        val code = """object A { val f = 100f; val b = 0 == f } """
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
