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

          def foo[K <: Option[String]](in: K): Boolean = {
            in == Some(1) // warning 5
            in == SomeValueClass(3) // warning 6
            in == Some("somestring") // warning 7 (ideally we could avoid this)
            in == Option("somestring") // ok
          }

          def foo2[K](in: K): Boolean = {
            in == Some(1) // warning 8
            in == SomeValueClass(3) // warning 9
            in == in // ok
          }

          def foo3[K <: J, J](in: K, in2: J): Boolean = {
            in == Some(1) // warning 10
            in2 == Some(1) // warning 11
            in == in // ok
            in == in2 // ok
            in2 == in2 // ok
          }

          def foo4[K <: Option[Option[String]]](in: K): Boolean = {
            in == Some(Some(1)) // warning 12
            in == SomeValueClass(3) // warning 13
            in == Some(Some("somestring")) // warning 14 (ideally we could avoid this)
            in == Option(Option("somestring")) // ok
          }

          Some(1) == Some("somestring") // warning 15
          Option(Option("1")) == Some(Some("2")) // ok
        } """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 15
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
