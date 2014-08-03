package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptySynchronizedBlock extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(_, TermName("synchronized")), _), List(Literal(Constant(())))) =>
            context.warn("Empty synchronized block", tree.pos, Levels.Warning, tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}