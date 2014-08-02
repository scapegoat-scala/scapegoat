package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Feedback}

import scala.tools.nsc.Global

/** @author Stephen Samuel
  *
  *         Inspired by IntelliJ
  */
class FilterSize extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(Apply(Select(_, TermName("filter")), _), TermName("size")) =>
          feedback.warn("filter().size() instead of count()", tree.pos, Levels.Info,
              ".filter(x => Bool).size can be replaced with count(x => Bool): " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }

}
