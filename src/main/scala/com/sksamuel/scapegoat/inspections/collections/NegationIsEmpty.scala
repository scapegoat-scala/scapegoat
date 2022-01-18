package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class NegationIsEmpty
    extends Inspection(
      text = "!isEmpty can be replaced with nonEmpty",
      defaultLevel = Levels.Info,
      description = "Checks whether !isEmpty can be replaced with nonEmpty.",
      explanation = "!.isEmpty can be replaced with.nonEmpty to make it easier to reason about."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val IsEmpty = TermName("isEmpty")
          private val Bang = TermName("unary_$bang")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Select(lhs, IsEmpty), Bang) if isIterable(lhs) =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
