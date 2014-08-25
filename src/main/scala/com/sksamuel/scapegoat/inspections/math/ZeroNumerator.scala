package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{ Levels, Inspector, InspectionContext, Inspection }

/** @author Stephen Samuel */
class ZeroNumerator extends Inspection {
  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._
      import definitions._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Literal(Constant(0)), TermName("$div")), args) =>
            context.warn("Zero numerator",
              tree.pos,
              Levels.Warning,
              "Dividing zero by any number will always return zero",
              ZeroNumerator.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
