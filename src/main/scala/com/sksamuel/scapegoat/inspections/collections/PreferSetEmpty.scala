package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class PreferSetEmpty extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val SetTerm = TermName("Set")
      private val ApplyTerm = TermName("apply")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(Select(_, SetTerm), ApplyTerm), _), List()) =>
            context.warn("Prefer Set.empty", tree.pos, Levels.Info,
              "Set[T]() creates a new instance. Consider Set.empty which does not allocate a new object. " +
                tree.toString().take(500), PreferSetEmpty.this)
          case _ => continue(tree)
        }
      }
    }
  }
}