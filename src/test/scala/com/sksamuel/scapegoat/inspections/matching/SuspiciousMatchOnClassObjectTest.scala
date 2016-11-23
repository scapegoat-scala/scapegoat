package com.sksamuel.scapegoat.inspections.matching

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

class SuspiciousMatchOnClassObjectTest
    extends FreeSpec
    with Matchers
    with PluginRunner
    with OneInstancePerTest {

  override val inspections = Seq(new SuspiciousMatchOnClassObject)

  "SuspiciousMatchOnClassObject" - {
    "should report warning" - {
      "for matching on object for case class with params" in {

        val code = """
                      trait Machine
                      case class Terminator(name:String) extends Machine

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
      "for matching on object for case class with no params" in {

        val code = """
                      trait Machine
                      case class Terminator() extends Machine

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
      "for top level case objects" in {
        val code = """package com.sammy

                      trait Android
                      case object Lal extends Android
                      case object Data extends Android
                      case class Robot(name:String) extends Android

                      object Test {
                        val b : Any = Data
                        b match {
                           case Data => println("Yes captain")
                           case Lal => println("Yes dad")
                           case Robot(name) => println(name)
                           case _ =>
                        }
                    } """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for top level objects" in {
        val code = """
                     |package com.sammy
                     |
                     |trait TestTrait
                     |object TestObject extends TestTrait
                     |
                     |object Go {
                     |  def test(t: TestTrait): Unit = t match {
                     |    case TestObject ⇒ println("ok")
                     |  }
                     |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for nested case objects" in {
        val code = """
                      package com.sammy
                     |object Go {
                     |  trait TestTrait
                     |  object TestObject extends TestTrait
                     |
                     |  def test(t: TestTrait): Unit = t match {
                     |    case TestObject ⇒ println("ok")
                     |  }
                     |} """.stripMargin

        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for case objects with abstract companion class" in {
        val code = """        abstract class Kill
                     |        case object Kill extends Kill
                     |        object A {
                     |          val a: AnyRef = "sam"
                     |          a match {
                     |            case Kill =>
                     |            case _ =>
                     |          }
                     |        }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
      "for instances of case class" in {
        val code = """        case class Robot(name:String)
                     |        object A {
                     |          val Robbie = Robot("Robbie")
                     |          val a: AnyRef = Robot("t800")
                     |          a match {
                     |            case Robbie =>
                     |            case Robot(name) =>
                     |            case _ =>
                     |          }
                     |        }""".stripMargin
        compileCodeSnippet(code)
        compiler.scapegoat.feedback.warnings.size shouldBe 0
      }
    }
  }
}