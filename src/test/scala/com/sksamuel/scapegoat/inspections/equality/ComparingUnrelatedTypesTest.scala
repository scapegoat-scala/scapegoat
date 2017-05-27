package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat.PluginRunner

import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

/** @author Stephen Samuel */
class ComparingUnrelatedTypesTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new ComparingUnrelatedTypes)

  private def verifyNoWarnings(code: String): Unit = {
    compileCodeSnippet(code)
    compiler.scapegoat.feedback.warnings.size shouldBe 0
  }

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

          val f: Float = 1.0f
          val d: Double = 1.0d
          f == 1 // warning 16
          d == 1 // warning 17

          val c: Char = 'a'
          c == -1 // warning 18
          c == 65536 // warning 19

          val i: Int = 1
          i == -2147483649L // warning 20
        } """.stripMargin
      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 20
    }
    "should not report warning" - {
      "for zero" - {
        "compared to long" in { verifyNoWarnings("""object A { val l = 100l; val b = 0 == l }""") }
        "compared to double" in { verifyNoWarnings("""object A { val d = 100d; val b = 0 == d }""") }
        "compared to float" in { verifyNoWarnings("""object A { val f = 100f; val b = 0 == f }""") }
      }
      "for long" - {
        "compared to zero" in { verifyNoWarnings("""object A { val l = 100l; val b = l == 0 }""") }
        "compared to int literal" in { verifyNoWarnings("""object A { val l = 100l; val b = l == 100 }""") }
      }
      "for char" - {
        "compared to char-sized long literal" in { verifyNoWarnings("""object A { val c = 'a'; val c = l == 97L }""") }
        "compared to char-sized int literal" in { verifyNoWarnings("""object A { val c = 'a'; val c = l == 97 }""") }
      }
      "for short" - {
        "compared to Short.MaxValue as int literal" in { verifyNoWarnings("""object A { val s = 1.toShort; val b = s == 32767 }""") }
      }
      "for double" - {
        "compared to zero" in { verifyNoWarnings("""object A { val d = 100d; val b = d == 0 }""") }
      }
      "for float" - {
        "compared to zero" in { verifyNoWarnings("""object A { val f = 100f; val b = f == 0 }""") }
      }
    }
  }
}
