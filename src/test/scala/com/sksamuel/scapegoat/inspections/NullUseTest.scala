package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{PluginRunner, Reporter}
import org.scalatest.{OneInstancePerTest, FreeSpec, Matchers}

/** @author Stephen Samuel */
class NullUseTest extends FreeSpec with ASTSugar with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new NullUse)

  import scala.reflect.runtime.{currentMirror => m, universe => u}
  import scala.tools.reflect.ToolBox

  val rep = new Reporter()
  val tb = m.mkToolBox()

  "null use" - {
    "should report warning" in {
      val expr = u.reify {
        println(null)
      }
      println(u showRaw expr.tree)
      new NullUse().traverser(rep).traverse(expr.tree)
      rep.warnings.size shouldBe 1
    }
    "should have full snippet for method param" in {
      val expr = u.reify {
        println(null)
      }
      println(u showRaw expr.tree)
      new NullUse().traverser(rep).traverse(expr.tree)
      rep.warnings.size shouldBe 1
      rep.warnings.forall(_.snippet.get.contains("method argument"))
    }
    "should handle override val in case class" in {
      val code = """abstract class Super(val name: String)
                    case class Boo(override val name: String) extends Super(name) """
      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 0
    }
    "should handler var args" in {
      val code = """class Birds(names:String*))"""
      compileCodeSnippet(code)
      compiler.scapegoat.reporter.warnings.size shouldBe 0
    }
  }
}