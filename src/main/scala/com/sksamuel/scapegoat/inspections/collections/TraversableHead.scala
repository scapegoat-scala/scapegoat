package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class TraversableHead extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def traverse(tree: Tree): Unit = {
        tree match {
          case Select(left, TermName("head")) =>
            if (left.tpe <:< typeOf[Traversable[_]])
              context.warn("Use of Traversable.head", tree.pos, Levels.Error, tree.toString().take(500))
          case _ => super.traverse(tree)
        }
      }
    }
  }
}