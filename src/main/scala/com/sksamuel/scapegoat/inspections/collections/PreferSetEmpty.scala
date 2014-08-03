package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class PreferSetEmpty extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def traverse(tree: Tree): Unit = {
        tree match {
          case TypeApply(Select(Select(_, TermName("Set")), TermName("apply")), _) =>
            context.warn("Prefer Set.empty", tree.pos, Levels.Info,
              "Set[T]() creates a new instance. Consider Set.empty which does not allocate a new object. " +
                tree.toString().take(500))
          case _ => super.traverse(tree)
        }
      }
    }
  }
}