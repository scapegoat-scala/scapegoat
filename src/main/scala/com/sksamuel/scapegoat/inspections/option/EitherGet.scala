package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class EitherGet
    extends Inspection(
      text = "Use of Either.right or Either.left projection followed by a get",
      defaultLevel = Levels.Error,
      description = "Checks for use of .get on Left or Right projection.",
      explanation =
        "Method .get on a Left and a Right projection is deprecated since 2.13, use Either.getOrElse or Either.swap.getOrElse instead."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Select(_, TermName("right")), TermName("get")) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case Select(Select(_, TermName("left")), TermName("get")) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
