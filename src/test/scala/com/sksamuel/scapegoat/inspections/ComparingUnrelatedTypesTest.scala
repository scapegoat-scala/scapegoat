package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.Reporter
import org.scalatest.{Matchers, FreeSpec}

/** @author Stephen Samuel */
class ComparingUnrelatedTypesTest extends FreeSpec with ASTSugar with Matchers {

  import scala.reflect.runtime.{universe => u}
  import scala.reflect.runtime.{currentMirror => m}
  import scala.tools.reflect.ToolBox

  val reporter = new Reporter()
  val tb = m.mkToolBox()

  "unrelated types" - {
    "when comparing equality" - {
      "should err" in {
        val expr = u.reify {
          val a = ""
          val b = 4
          val equality = a == b
        }
        println(u showRaw expr.tree)
        ComparingUnrelatedTypes.analyze(expr.tree, reporter)
        reporter.warnings.size shouldBe 1
      }
    }
  }
}
