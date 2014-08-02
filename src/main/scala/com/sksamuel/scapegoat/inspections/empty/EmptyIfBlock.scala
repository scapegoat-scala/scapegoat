package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class EmptyIfBlock extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._
    override def traverse(tree: Tree): Unit = {
      tree match {
        case If(_, Literal(Constant(())), _) =>
          feedback.warn("Empty if statement", tree.pos, level = Levels.Warning, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
