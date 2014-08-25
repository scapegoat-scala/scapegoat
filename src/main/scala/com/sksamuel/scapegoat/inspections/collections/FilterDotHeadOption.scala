package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class FilterDotHeadOption extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Apply(Select(_, TermName("filter")), _), TermName("headOption")) =>
            context.warn("filter().headOption instead of find()", tree.pos, Levels.Info,
              ".filter(x => Bool).headOption can be replaced with find(x => Bool): " + tree.toString().take(500), FilterDotHeadOption.this)
          case _ => continue(tree)
        }
      }
    }
  }
}