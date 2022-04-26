package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class FilterDotIsEmpty
    extends Inspection(
      text = "filter().isEmpty instead of !exists()",
      defaultLevel = Levels.Info,
      description = "Checks for use of filter().isEmpty.",
      explanation =
        "`filter()` scans the entire collection, which can potentially be avoided if the element exists in the collection - `filter().isEmpty` can be replaced with `!exists()`."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Apply(Select(_, TermName("filter")), _), TermName("isEmpty")) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
