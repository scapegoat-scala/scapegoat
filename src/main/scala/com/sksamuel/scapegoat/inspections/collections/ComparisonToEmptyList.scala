package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class ComparisonToEmptyList
    extends Inspection(
      text = "Comparison to empty list",
      defaultLevel = Levels.Info,
      description = "Checks for code like `a == List()` or `a == Nil`.",
      explanation = "Prefer use of `isEmpty` instead of comparison to an empty List."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Equals = TermName("$eq$eq")
          private val Empty = TermName("empty")
          private val TermApply = TermName("apply")
          private val TermNil = TermName("Nil")
          private val TermList = TermName("List")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(_, Equals), List(Select(_, TermNil))) => warn(tree)
              case Apply(Select(Select(_, TermNil), Equals), _)       => warn(tree)
              case Apply(Select(_, Equals), List(TypeApply(Select(Select(_, TermList), Empty), _))) =>
                warn(tree)
              case Apply(Select(TypeApply(Select(Select(_, TermList), Empty), _), Equals), _) => warn(tree)
              case Apply(
                    Select(_, Equals),
                    List(Apply(TypeApply(Select(Select(_, TermList), TermApply), _), Nil))
                  ) =>
                warn(tree)
              case Apply(
                    Select(Apply(TypeApply(Select(Select(_, TermList), TermApply), _), Nil), Equals),
                    _
                  ) =>
                warn(tree)
              case _ => continue(tree)
            }
          }

          private def warn(tree: Tree): Unit =
            context.warn(tree.pos, self, tree.toString.take(200))
        }
    }
}
