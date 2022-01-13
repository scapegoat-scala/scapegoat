package com.sksamuel.scapegoat.inspections.inference

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class PointlessTypeBounds
    extends Inspection(
      text = "Pointless type bounds",
      defaultLevel = Levels.Warning,
      description = "Finds type bounds of the form `A <: Any` or `A >: Nothing`.",
      explanation = "Type bound resolves to `Nothing <: T <: Any`. Did you mean to put in other bounds?"
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case TypeDef(_, _, _, rhs)
                  if rhs.tpe.bounds.isEmptyBounds
                    && rhs.pos != null
                    && (rhs.pos.lineContent
                      .contains("<: Any") || rhs.pos.lineContent.contains(">: Nothing")) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
