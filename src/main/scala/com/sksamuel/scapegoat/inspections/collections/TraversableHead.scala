package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class TraversableHead extends Inspection("Use of Traversable.head", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(left, TermName("head")) =>
            if (isTraversable(left))
              context.warn(tree.pos, self, tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}