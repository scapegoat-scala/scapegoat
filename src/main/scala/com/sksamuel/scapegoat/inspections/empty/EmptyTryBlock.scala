package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyTryBlock extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def traverse(tree: Tree): Unit = {
        tree match {
          case Try(Literal(Constant(())), _, _) =>
            context.warn("Empty try block", tree.pos, Levels.Warning, tree.toString().take(500))
          case _ => super.traverse(tree)
        }
      }
    }
  }
}