package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class FilterIsEmpty extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(Apply(Select(_, TermName("filter")), _), TermName("isEmpty")) =>
          feedback.warn("filter().isEmpty instead of !exists()", tree.pos, Levels.Info,
            ".filter(x => Bool).isEmpty can be replaced with !exists(x => Bool): " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
