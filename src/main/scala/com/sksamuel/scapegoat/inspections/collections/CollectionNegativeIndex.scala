package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class CollectionNegativeIndex extends Inspection("Collection index out of bounds", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("apply")), List(Literal(Constant(x: Int)))) if isList(lhs) && x < 0 =>
            context.warn(tree.pos, self, tree.toString().take(100))
          case _ => continue(tree)
        }
      }
    }
  }
}
