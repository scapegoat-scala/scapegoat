package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class NegativeSeqPad extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(lhs, TermName("padTo")), _), Literal(Constant(x)) :: tail) =>
            context.warn("Negative seq padTo", tree.pos, Levels.Error, tree.toString().take(500), NegativeSeqPad.this)
          case _ => continue(tree)
        }
      }
    }
  }
}