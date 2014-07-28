package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

/** @author Stephen Samuel */
class NullUse extends Inspection {

  import scala.reflect.runtime.universe
  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Literal(Constant(null)) => reporter.warn("null use", tree, Levels.Error,
          "null used near: " + tree.toString().take(300))
        case _ => super.traverse(tree)
      }
    }
  }
}
