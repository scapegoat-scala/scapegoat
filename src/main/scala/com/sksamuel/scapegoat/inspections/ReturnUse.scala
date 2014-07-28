package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.runtime.universe._
import scala.reflect.runtime._

/** @author Stephen Samuel */
class ReturnUse extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: universe.Tree): Unit = {
      tree match {
        case Return(expr) => reporter.warn("Use of Return", tree, level = Levels.Error)
        case _ => super.traverse(tree)
      }
    }
  }
}
