package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class NegativeSeqPad extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def traverse(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("padTo")), Literal(Constant(x)) :: tail) =>
            context.warn("Negative seq padTo", tree.pos, Levels.Error, tree.toString().take(500))
          case _ => super.traverse(tree)
        }
      }
    }
  }
}