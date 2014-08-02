package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Feedback, Inspection}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class FilterHeadOption extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(Apply(Select(_, TermName("filter")), _), TermName("headOption")) =>
          feedback.warn("filter().headOption instead of find()", tree.pos, Levels.Info,
            ".filter(x => Bool).headOption can be replaced with find(x => Bool): " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
