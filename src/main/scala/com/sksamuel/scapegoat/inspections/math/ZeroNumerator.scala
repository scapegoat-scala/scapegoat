package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class ZeroNumerator extends Inspection("Zero numerator", Levels.Warning,
  "Dividing zero by any number will always return zero") {
  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Literal(Constant(0)), TermName("$div")), _) =>
            context.warn(tree.pos, self)
          case _ => continue(tree)
        }
      }
    }
  }
}
