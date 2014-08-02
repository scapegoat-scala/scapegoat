package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class EmptySynchronizedBlock extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(TypeApply(Select(_, TermName("synchronized")), _), List(Literal(Constant(())))) =>
          feedback.warn("Empty synchronized block", tree.pos, Levels.Warning, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
