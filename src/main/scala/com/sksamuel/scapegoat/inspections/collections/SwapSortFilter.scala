package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class SwapSortFilter
    extends Inspection(
      text = "Swap sort filter",
      defaultLevel = Levels.Info,
      description = "Checks for an inefficient use of filter.sort.",
      explanation =
        "Filter first and then sort the remaining collection. Swap sort.filter for filter.sort for better performance."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(
                    Select(Apply(TypeApply(Select(lhs, TermName("sorted")), _), _), TermName("filter")),
                    _
                  ) if isSeq(lhs) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case Apply(
                    Select(
                      Apply(Apply(TypeApply(Select(lhs, TermName("sortBy")), _), _), _),
                      TermName("filter")
                    ),
                    _
                  ) if isSeq(lhs) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case Apply(Select(Apply(Select(lhs, TermName("sortWith")), _), TermName("filter")), _)
                  if isSeq(lhs) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
