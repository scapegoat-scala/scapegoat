package com.sksamuel.scapegoat.inspections.names

import com.sksamuel.scapegoat.PluginRunner
import com.sksamuel.scapegoat.inspections.naming.ClassNames
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

/** @author Stephen Samuel */
class ClassNamesTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new ClassNames)

  "ClassNames" - {
    "should report warning" - {
      "for classes beginning with lowercase" in {
        val code =
          """class aClass
            |case class bClass()
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
      "for classes containing underscore" in {
        val code =
          """class My_class
            |case class Your_class()
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 2
      }
    }
    "should not report warning" - {
      "for anon classes" in {
        val code =
          """
            |class Test {
            |    import java.util.{Observer, Observable}
            |    val observable = new Observable {}
            |    observable.addObserver(new Observer {
            |      override def update(o: Observable, arg: scala.Any): Unit = ()
            |    })
            |}
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for implicit classes" in {
        val code =
          """
            |class Test {
            |  implicit class ClintEaswood(name:String) {
            |    def magnum = "boom"
            |  }
            |  "sammy".magnum
            |}
          """.stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}
