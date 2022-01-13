package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class EmptySynchronizedBlock
    extends Inspection(
      text = "Empty synchronized block",
      defaultLevel = Levels.Warning,
      description = "Checks for empty synchronized blocks.",
      explanation = "An empty synchronized block is considered as dead code."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Sync = TermName("synchronized")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(TypeApply(Select(_, Sync), _), List(Literal(Constant(())))) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
