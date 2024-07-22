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

  def inspector(ctx: InspectionContext): Inspector =
    new Inspector(ctx) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case TypeDef(_, tn, _, rhs)
                  if rhs.tpe.bounds.isEmptyBounds
                    && rhs.pos != null
                    && (rhs.pos.lineContent.matches(
                      s".*${tn}\\s*<:\\s*Any\\s*[,:\\]].*"
                    ) || rhs.pos.lineContent.contains(">: Nothing")) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
