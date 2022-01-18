package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 *
 * Inspired by IntelliJ
 */
class FilterDotSize
    extends Inspection(
      text = "filter().size() instead of count()",
      defaultLevel = Levels.Info,
      description = "Checks if filter().size can be simplified to count().",
      explanation = "`filter().size` can be replaced with `count()`, which is more concise."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Apply(Select(_, TermName("filter")), _), TermName("size")) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
