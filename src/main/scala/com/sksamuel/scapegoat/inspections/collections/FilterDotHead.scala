package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class FilterDotHead
    extends Inspection(
      text = "filter().head can throw an exception",
      defaultLevel = Levels.Info,
      description = "Checks for use of filter().head.",
      explanation =
        "`filter().head` can throw an exception if the collection is empty - it can be replaced with `find() match {...}`."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Filter = TermName("filter")
          private val Head = TermName("head")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Apply(Select(_, Filter), _), Head) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
