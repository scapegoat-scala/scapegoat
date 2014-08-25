package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyTryBlock extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(Literal(Constant(())), _, _) =>
            context.warn("Empty try block", tree.pos, Levels.Warning, tree.toString().take(500), EmptyTryBlock.this)
          case _ => continue(tree)
        }
      }
    }
  }
}