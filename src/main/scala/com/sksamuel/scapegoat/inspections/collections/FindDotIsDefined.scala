package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class FindDotIsDefined
    extends Inspection(
      text = "find().isDefined() instead of exists()",
      defaultLevel = Levels.Info,
      description = "Checks whether `find()` can be replaced with `exists()`.",
      explanation = "`find().isDefined` can be replaced with `exists()`, which is more concise."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Apply(Select(_, TermName("find")), _), TermName("isDefined")) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
