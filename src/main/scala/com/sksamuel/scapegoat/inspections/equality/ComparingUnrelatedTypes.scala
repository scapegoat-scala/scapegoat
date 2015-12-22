package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ComparingUnrelatedTypes extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("$eq$eq")), List(Literal(Constant(0)))) if lhs.tpe.typeSymbol.isNumericValueClass =>
          case Apply(Select(Literal(Constant(0)), TermName("$eq$eq")), List(rhs)) if rhs.tpe.typeSymbol.isNumericValueClass =>
          case Apply(Select(lhs, TermName("$eq$eq")), List(rhs)) =>
            val (l, r) = if (lhs.tpe.typeSymbol.asClass.isDerivedValueClass || rhs.tpe.typeSymbol.asClass.isDerivedValueClass) {
              (lhs.tpe, rhs.tpe)
            } else {
              (lhs.tpe.erasure, rhs.tpe.erasure)
            }

            if (!(l <:< r || r <:< l || l =:= r)) {
              context.warn("Comparing unrelated types", tree.pos, Levels.Error, tree.toString().take(500), ComparingUnrelatedTypes.this)
            }
          case _ => continue(tree)
        }
      }
    }
  }
}
