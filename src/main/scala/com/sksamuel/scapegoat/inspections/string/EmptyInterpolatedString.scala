package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class EmptyInterpolatedString extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(lhs, TermName("apply")), List(string)), TermName("s")), Nil) =>
          feedback.warn("Empty interpolated string", tree.pos, Levels.Warning, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}

