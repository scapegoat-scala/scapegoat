package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.Reporter
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class NullUseTest extends FreeSpec with ASTSugar with Matchers {

  import scala.reflect.runtime.{currentMirror => m, universe => u}
  import scala.tools.reflect.ToolBox

  val reporter = new Reporter()
  val tb = m.mkToolBox()

  "null use" - {
    "should report warning" in {
      val expr = u.reify {
        synchronized {
          println("sammy")
        }
        synchronized {
        }
      }
      println(u showRaw expr.tree)
      new NullUse().traverser(reporter).traverse(expr.tree)
      reporter.warnings.size shouldBe 1
    }
    "should have full snippet for method param" in {
      val expr = u.reify {
        println(null)
      }
      println(u showRaw expr.tree)
      new NullUse().traverser(reporter).traverse(expr.tree)
      reporter.warnings.size shouldBe 1
      reporter.warnings.forall(_.snippet.get.contains("method argument"))
    }
  }
}
