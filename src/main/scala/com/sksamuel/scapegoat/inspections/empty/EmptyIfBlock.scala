package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 */
class EmptyIfBlock
    extends Inspection(
      text = "Empty if expression",
      defaultLevel = Levels.Warning,
      description = "Checks for empty if blocks.",
      explanation = "An empty if block is considered as dead code."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case If(_, Literal(Constant(())), _) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
