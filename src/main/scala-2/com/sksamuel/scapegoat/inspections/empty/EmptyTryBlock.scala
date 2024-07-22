package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class EmptyTryBlock
    extends Inspection(
      text = "Empty try block",
      defaultLevel = Levels.Warning,
      description = "Checks for empty try blocks.",
      explanation = "An empty try block is considered as dead code."
    ) {

  def inspector(ctx: InspectionContext): Inspector =
    new Inspector(ctx) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Try(Literal(Constant(())), _, _) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
