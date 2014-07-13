package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.Reporter
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class NullUseInspectionTest extends FreeSpec with ASTSugar with Matchers {

  import scala.reflect.runtime.{currentMirror => m, universe => u}
  import scala.tools.reflect.ToolBox

  val reporter = new Reporter()
  val tb = m.mkToolBox()

  "null use" - {
    "should report warning" in {
      val expr = u.reify {
        val b = null
        println(b)
      }
      println(u showRaw expr.tree)
      NullUseInspection.traverser(reporter).traverse(expr.tree)
      reporter.warnings.size shouldBe 1
    }
  }
}
