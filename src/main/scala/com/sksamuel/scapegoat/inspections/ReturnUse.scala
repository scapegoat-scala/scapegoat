package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime.universe._

/** @author Stephen Samuel */
class ReturnUse extends Inspection {
  override def traverser(reporter: Reporter) = new Traverser with SuppressAwareTraverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case Return(expr) => reporter.warn("Use of Return", tree, Levels.Error)
        case _ => super.traverse(tree)
      }
    }
  }
}
