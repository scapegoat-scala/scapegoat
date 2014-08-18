package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class PreferVectorEmpty extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply  new context.Traverser {

      import context.global._

      private val ApplyTerm = TermName("apply")
      private val SeqTerm = TermName("Vector")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(Select(_, SeqTerm), ApplyTerm), _), List()) => warn(tree)
          case _ => continue(tree)
        }
      }

      private def warn(tree: Tree) {
        context.warn("Prefer Vector.empty", tree.pos, Levels.Info,
          "Vector[T]() creates a new instance. Consider Vector.empty which does not allocate a new object. " +
            tree.toString().take(500), PreferVectorEmpty.this)
      }
    }
  }
}