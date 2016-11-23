package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{ FreeSpec, Matchers, OneInstancePerTest }

class VarUseTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new VarUse)

  "var keyword use" - {
    "should report warning" in {
      val code = """class Test {
                      def hello : String = "sammy"
                      var name = hello
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
//    "should not report warning" - {
//      "for akka actors" in {
//        addToClassPath("com.typesafe.akka", "akka-actor_2.11", "2.3.4")
//        val code = """import akka.actor.Actor
//                     |import akka.actor.Actor.Receive
//                     |  class TestActor extends Actor {
//                     |    var awesome = false
//                     |    override def receive: Receive = {
//                     |      case _ => awesome = !awesome
//                     |    }
//                     |  }""".stripMargin
//        compileCodeSnippet(code)
//        compiler.scapegoat.feedback.warnings.size shouldBe 0
//      }
//    }
  }
  //  "async macros" - {
  //    "should be ignored" in {
  //      val code =
  //        """
  //          |      import scala.async.Async._
  //          |      import scala.concurrent.Future
  //          |      import scala.concurrent.ExecutionContext.Implicits.global
  //          |      object Test {
  //          |       val result = async {
  //          |       val a = await( Future { 1 } )
  //          |       val b = await( Future { 2 } )
  //          |       a + b
  //          |       }
  //          |      }
  //        """.stripMargin
  //      addToClassPath("org.scala-lang.modules", "scala-async_2.11", "0.9.2")
  //      compileCodeSnippet(code)
  //      compiler.scapegoat.feedback.warnings.size shouldBe 0
  //    }
  //  }

  // travis doesn't seem to find scala xml on the classpath so disabling
  //  "xml variables" - {
  //    "should not report warning" in {
  //      addToClassPath("org.scala-lang.modules", "scala-xml_2.11", "1.0.2")
  //      val code = """class Test {
  //
  //                      import scala.xml.Elem
  //
  //                      trait LineItem {
  //                        def xml
  //                      }
  //
  //                   |  def example1(str: String, nodeName: String, first: Seq[LineItem], second: Seq[LineItem]): Elem = {
  //                   |    // "copy" is needed here, as <s:$nodeName> is not a valid XML literal in scala
  //                   |    <xml>
  //                   |      <s:State>{str}</s:State>
  //                   |      {first.map(_.xml)}
  //                   |      {second.map(_.xml)}
  //                   |    </xml>
  //                   |      .copy(prefix = "s", label = nodeName)
  //                   |  }
  //                   |
  //                   |  def example2(contentType: String, mtomPartContentId: String): Elem = {
  //                   |    <ser:Data xmlns:ns4="http://www.w3.org/2005/05/xmlmime"
  //                   |              ns4:contentType={contentType.toString}>
  //                   |      <inc:Include href={s"cid:$mtomPartContentId"} xmlns:inc="http://www.w3.org/2004/08/xop/include"/>
  //                   |    </ser:Data>
  //                   |  }
  //                      } """.stripMargin
  //
  //      compileCodeSnippet(code)
  //      compiler.scapegoat.feedback.warnings.size shouldBe 0
  //    }
  //  }
}
