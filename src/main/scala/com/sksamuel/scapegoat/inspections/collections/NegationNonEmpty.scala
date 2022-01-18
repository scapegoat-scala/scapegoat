package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class NegationNonEmpty
    extends Inspection(
      text = "!nonEmpty can be replaced with isEmpty",
      defaultLevel = Levels.Info,
      description = "Checks whether !nonEmpty can be replaced with isEmpty.",
      explanation = "!.nonEmpty can be replaced with.isEmpty to make it easier to reason about."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val IsEmpty = TermName("nonEmpty")
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
