package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class PreferMapEmpty extends Inspection("Prefer Map.empty", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val MapTerm = TermName("Map")
      private val ApplyTerm = TermName("apply")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(Select(_, MapTerm), ApplyTerm), _), List()) =>
            context.warn(tree.pos, self,
              "Map[K,V]() creates a new instance. Consider Map.empty which does not allocate a new object. " +
                tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}