package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class FilterDotIsEmpty extends Inspection("filter().isEmpty instead of !exists()", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Apply(Select(_, TermName("filter")), _), TermName("isEmpty")) =>
            context.warn(tree.pos, self,
              ".filter(x => Bool).isEmpty can be replaced with !exists(x => Bool): " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}