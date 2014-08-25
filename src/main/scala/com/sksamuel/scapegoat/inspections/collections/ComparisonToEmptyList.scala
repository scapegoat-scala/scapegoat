package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class ComparisonToEmptyList extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Equals = TermName("$eq$eq")
      private val Empty = TermName("empty")
      private val Nil = TermName("Nil")
      private val TermList = TermName("List")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(_, Equals), List(Select(_, Nil))) => warn(tree)
          case Apply(Select(Select(_, Nil), Equals), _) => warn(tree)
          case Apply(Select(_, Equals), List(TypeApply(Select(Select(_, TermList), Empty), _))) => warn(tree)
          case Apply(Select(TypeApply(Select(Select(_, TermList), Empty), _), Equals), _) => warn(tree)
          case _ => continue(tree)
        }
      }

      private def warn(tree: Tree) {
        context.warn("Comparison to empty list",
          tree.pos,
          Levels.Info,
          "Prefer use of isEmpty instead of comparison to an empty List: " + tree.toString().take(200),
          ComparisonToEmptyList.this)
      }
    }
  }
}