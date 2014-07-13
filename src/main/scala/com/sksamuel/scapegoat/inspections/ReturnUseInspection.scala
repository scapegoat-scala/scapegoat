package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Reporter}

import scala.reflect.runtime.universe._
import scala.reflect.runtime._

/** @author Stephen Samuel */
object ReturnUseInspection extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: universe.Tree): Unit = {
      tree match {
        case Return(expr) => reporter.warn("Use of Return", tree.pos.line)
        case _ => super.traverse(tree)
      }
    }
  }
}
