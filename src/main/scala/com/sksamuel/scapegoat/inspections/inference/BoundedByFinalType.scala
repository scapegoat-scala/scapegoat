package com.sksamuel.scapegoat.inspections.inference

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class BoundedByFinalType
    extends Inspection(
      text = "Bounded by a final type",
      defaultLevel = Levels.Warning,
      description = "Checks for types with upper bounds of a final type.",
      explanation = "Pointless type bound. Type parameter can only be a single value."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._
          import definitions._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case dd @ DefDef(_, _, _, _, _, _)
                  if dd.symbol != null && dd.symbol.owner.tpe.baseClasses.contains(PartialFunctionClass) =>
              case tdef: TypeDef if tdef.symbol.isAliasType =>
              case TypeDef(_, _, _, typeTree: TypeTree) =>
                typeTree.original match {
                  case TypeBoundsTree(lo, hi) if lo.tpe.isFinalType && hi.tpe.isFinalType =>
                    context.warn(tree.pos, self, tree.toString.take(300))
                  case _ =>
                }
              case _ => continue(tree)
            }
          }
        }
    }
}
