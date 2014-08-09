package com.sksamuel.scapegoat.inspections.inference

import com.sksamuel.scapegoat.{Levels, Inspection, InspectionContext, Inspector}

/** @author Stephen Samuel */
class BoundedByFinalType extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case t@TypeDef(_, _, _, rhs) if rhs.tpe.bounds.hi.isFinalType =>
            context.warn("Bounded by final type", tree.pos, Levels.Warning,
              "Pointless type bound by final type. Type parameter can only be a single value: " + tree.toString().take(300),
              BoundedByFinalType.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
