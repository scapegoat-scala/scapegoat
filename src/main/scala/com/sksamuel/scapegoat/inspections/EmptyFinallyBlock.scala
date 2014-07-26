package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Level, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class EmptyFinallyBlock extends Inspection {

  override def level: Level = Levels.Medium

  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case universe.Try(body, catches, finalizer) =>
          if (finalizer.children.isEmpty)
            reporter.warn("Empty finalizer", tree.pos.line, "Empty finalizer near " + body.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
