package com.sksamuel.scapegoat.inspections.inference

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class BoundedByFinalType extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._
      import definitions._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case dd @ DefDef(mods, _, _, _, _, _) if dd.symbol != null && dd.symbol.owner.tpe.baseClasses.contains(PartialFunctionClass) =>
          case tdef: TypeDef if tdef.symbol.isAliasType =>
          case TypeDef(_, _, _, typeTree: TypeTree) =>
            typeTree.original match {
              case TypeBoundsTree(lo, hi) if lo.tpe.isFinalType && hi.tpe.isFinalType =>
                context.warn("Bounded by final type", tree.pos, Levels.Warning,
                  "Pointless type bound. Type parameter can only be a single value: " + tree
                    .toString()
                    .take(300),
                  BoundedByFinalType.this)
              case _ =>
            }
          case _ => continue(tree)
        }
      }
    }
  }
}