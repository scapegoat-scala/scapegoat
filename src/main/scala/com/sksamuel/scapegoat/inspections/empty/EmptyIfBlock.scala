package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class EmptyIfBlock
    extends Inspection(
      text = "Empty if expression",
      defaultLevel = Levels.Warning,
      description = "Checks for empty if blocks.",
      explanation = "An empty if block is considered as dead code."
    ) {

  def inspector(ctx: InspectionContext): Inspector =
    new Inspector(ctx) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              // Function directly contains if, an empty block would generate Unit (or Any) output type
              case Function(_, If(_, Literal(Constant(())), _)) => // skip tree
              case If(_, Literal(Constant(())), _) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
