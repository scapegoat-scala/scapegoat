package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{OneInstancePerTest, FreeSpec, Matchers}

/** @author Stephen Samuel */
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
  }

  "xml variables" - {
    "should not report warning" in {
      addToClassPath("org.scala-lang.modules", "scala-xml_2.11", "1.0.2")
      val code = """class Test {

                    import scala.xml.Elem

                    trait LineItem {
                      def xml
                    }

                   |  def example1(str: String, nodeName: String, first: Seq[LineItem], second: Seq[LineItem]): Elem = {
                   |    // "copy" is needed here, as <s:$nodeName> is not a valid XML literal in scala
                   |    <xml>
                   |      <s:State>{str}</s:State>
                   |      {first.map(_.xml)}
                   |      {second.map(_.xml)}
                   |    </xml>
                   |      .copy(prefix = "s", label = nodeName)
                   |  }
                   |
                   |  def example2(contentType: String, mtomPartContentId: String): Elem = {
                   |    <ser:Data xmlns:ns4="http://www.w3.org/2005/05/xmlmime"
                   |              ns4:contentType={contentType.toString}>
                   |      <inc:Include href={s"cid:$mtomPartContentId"} xmlns:inc="http://www.w3.org/2004/08/xop/include"/>
                   |    </ser:Data>
                   |  }
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}
