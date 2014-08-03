package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ComparingUnrelatedTypes extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("$eq$eq")), List(rhs)) =>
            val q = lhs
            val l = lhs.tpe.erasure
            val r = rhs.tpe.erasure
            if (!(l <:< r || r <:< l || l =:= r)) {
              context.warn("Comparing unrelated types", tree.pos, Levels.Error, tree.toString().take(500), ComparingUnrelatedTypes.this)
            }
          case _ => continue(tree)
        }
      }
    }
  }
}
