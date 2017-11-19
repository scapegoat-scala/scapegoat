package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class PreferSeqEmpty extends Inspection("Prefer Seq.empty", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val ApplyTerm = TermName("apply")
      private val SeqTerm = TermName("Seq")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(Select(_, SeqTerm), ApplyTerm), _), List()) => warn(tree)
          case _ => continue(tree)
        }
      }

      private def warn(tree: Tree) {
        context.warn(tree.pos, self,
          "Seq[T]() creates a new instance. Consider Seq.empty which does not allocate a new object. " +
            tree.toString().take(500))
      }
    }
  }
}