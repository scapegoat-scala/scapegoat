package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.Reporter
import org.scalatest.{FreeSpec, Matchers}

/** @author Stephen Samuel */
class BrokenOddnessTest extends FreeSpec with ASTSugar with Matchers {

  import scala.reflect.runtime.{currentMirror => m, universe => u}
  import scala.tools.reflect.ToolBox

  val reporter = new Reporter()
  val tb = m.mkToolBox()

  "broken odd use" - {
    "should report warning" in {
      val expr = u.reify {
        var i = 15
        def odd(a: Int) = a % 2 == 1
        val odd2 = i % 2 == 1
      }
      println(u showRaw expr.tree)
      new BrokenOddness().traverser(reporter).traverse(expr.tree)
      reporter.warnings.size shouldBe 2
    }
  }
}
