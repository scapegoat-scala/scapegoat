package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Level, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class EmptyFinallyBlock extends Inspection {

  import universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case universe.Try(body, catches, finalizer) =>
          if (finalizer.children.isEmpty)
            reporter.warn("Empty finalizer", tree, level = Levels.Warning,
              "Empty finalizer near " + body.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
