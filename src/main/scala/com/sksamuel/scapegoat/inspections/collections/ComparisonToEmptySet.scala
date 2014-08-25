package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class ComparisonToEmptySet extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Equals = TermName("$eq$eq")
      private val Empty = TermName("empty")
      private val TermApply = TermName("apply")
      private val TermSet = TermName("Set")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(_, Equals), List(Apply(TypeApply(Select(Select(_, TermSet), TermApply), _), _))) => warn(tree)
          case Apply(Select(Apply(TypeApply(Select(Select(_, TermSet), TermApply), _), _), Equals), _) => warn(tree)
          case Apply(Select(_, Equals), List(TypeApply(Select(Select(_, TermSet), Empty), _))) => warn(tree)
          case Apply(Select(TypeApply(Select(Select(_, TermSet), Empty), _), Equals), _) => warn(tree)
          case _ => continue(tree)
        }
      }

      private def warn(tree: Tree) {
        context.warn("Comparison to empty list",
          tree.pos,
          Levels.Info,
          "Prefer use of isEmpty instead of comparison to an empty List: " + tree.toString().take(200),
          ComparisonToEmptySet.this)
      }
    }
  }
}