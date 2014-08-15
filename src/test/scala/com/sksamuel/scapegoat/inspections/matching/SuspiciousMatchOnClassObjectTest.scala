package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class SuspiciousMatchOnClassObjectTest
  extends FreeSpec
  with Matchers
  with PluginRunner
  with OneInstancePerTest {

  override val inspections = Seq(new SuspiciousMatchOnClassObject)

  "SuspiciousMatchOnClassObject" - {
    "should report warning" - {
      "for matching class object" in {

        val code = """
                      trait Machine
                      case class Terminator(name:String) extends Machine
                      case class Android(name:String) extends Machine
                      case class Man(name:String, gender:String) extends Machine

                      class Test {
                        def b : Any = 4
                        b match {
                           case Terminator =>
                           case _ =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 1
      }
    }
    "should not report warning" - {
      "for wildcard with types" in {
        val code = """
                      trait Machine
                      case class Terminator(name:String) extends Machine
                      case class Android(name:String) extends Machine
                      case class Man(name:String, gender:String) extends Machine

                      object Test {
                        val b : Any = Terminator("arnie")
                        b match {
                           case _ : Man =>
                           case _ =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for unapply" in {
        val code = """
                      trait Machine
                      case class Terminator(name:String) extends Machine
                      case class Android(name:String) extends Machine
                      case class Man(name:String, gender:String) extends Machine

                      object Test {
                        val b : Any = Terminator("arnie")
                        b match {
                           case Android("data") => println("yay data")
                           case _ =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}