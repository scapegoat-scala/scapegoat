package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class PreferSetEmpty extends Inspection("Prefer Set.empty", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val SetTerm = TermName("Set")
      private val ApplyTerm = TermName("apply")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(Select(_, SetTerm), ApplyTerm), _), List()) =>
            context.warn(tree.pos, self,
              "Set[T]() creates a new instance. Consider Set.empty which does not allocate a new object. " +
                tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}