package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class FilterDotHeadOption
    extends Inspection(
      text = "filter().headOption instead of find()",
      defaultLevel = Levels.Info,
      description = "Checks for use of filter().headOption.",
      explanation =
        "`filter()` scans the entire collection, which is unnecessary if you only want to get the first element that satisfies the predicate - `filter().headOption` can be replaced with `find()` to potentially avoid scanning the entire collection."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Apply(Select(_, TermName("filter")), _), TermName("headOption")) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
