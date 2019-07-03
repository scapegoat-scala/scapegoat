package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class TraversableLast extends Inspection("Use of Traversable.last", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(left, TermName("last")) =>
            if (isTraversable(left))
              context.warn(tree.pos, self, tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
