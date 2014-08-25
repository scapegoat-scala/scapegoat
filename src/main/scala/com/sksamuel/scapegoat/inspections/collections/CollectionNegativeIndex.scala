package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class CollectionNegativeIndex extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("apply")), List(Literal(Constant(x: Int)))) if lhs.tpe <:< typeOf[List[_]] && x < 0 =>
            context.warn("Collection index out of bounds",
              tree.pos,
              Levels.Warning,
              tree.toString().take(100),
              CollectionNegativeIndex.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
