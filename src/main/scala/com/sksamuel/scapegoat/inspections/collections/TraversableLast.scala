package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class TraversableLast extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(left, TermName("last")) =>
            if (left.tpe <:< typeOf[Traversable[_]])
              context.warn("Use of Traversable.last", tree.pos, Levels.Error, tree.toString().take(500), TraversableLast.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
