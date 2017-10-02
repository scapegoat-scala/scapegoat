package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ OneInstancePerTest, FreeSpec, Matchers }

import scala.concurrent.Future

/** @author Stephen Samuel */
class PartialFunctionInsteadOfMatchTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new PartialFunctionInsteadOfMatch)

  "when using match instead of partial function" - {
    "should report warning" in {

      val code = """object Test {

                     val list = List(1,"sam")
                     list.map(_ match {
                       case i: Int =>
                       case s: String =>
                     })
                     list.map(g => g match {
                      case i: Int =>
                      case s: String =>
                     })

                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 2
    }
  }

  "when using partial function instead of match" - {
    "should not report warning" in {

      val code = """object Test {

                    import scala.concurrent.Future
                    import scala.concurrent.ExecutionContext.Implicits.global

                     val list = List(1,"sam")
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }
                     list.map {
                       case i: Int =>
                       case s: String =>
                     }

                     val future = Future { }
                     future onComplete {
                       case _ =>
                     }
                     future onSuccess {
                      case _ =>
                     }


                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }

  "case class generated partial functions" - {
    "should not report warning" in {

      val code = """object Test {
                      sealed trait A
                      case class B() extends A
                      case class C() extends A
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }

  "for comprehensions" - {
    "should not report warning" in {

      val code = """object Test {
                      for (a <- Option("sam")) {
                        println(a)
                      }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
