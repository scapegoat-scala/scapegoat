package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class EmptyWhileBlock
    extends Inspection(
      text = "Empty while block",
      defaultLevel = Levels.Warning,
      description = "Checks for empty while blocks.",
      explanation = "An empty while block is considered as dead code."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case LabelDef(_, _, If(_, Block(List(Literal(Constant(()))), _), _)) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
