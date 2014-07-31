package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class EitherGet extends Inspection {
  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(Select(_, TermName("right")), TermName("get")) =>
          feedback.warn("Use of Either Right Projection get", tree.pos, Levels.Error, tree.toString().take(500))
        case Select(Select(_, TermName("left")), TermName("get")) =>
          feedback.warn("Use of Either Left Projection get", tree.pos, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
