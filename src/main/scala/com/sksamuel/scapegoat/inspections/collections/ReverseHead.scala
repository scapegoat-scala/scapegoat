package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class ReverseHead extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(_, TermName("reverse")), TermName("head")) =>
            context.warn("reverse.head instead of last", tree.pos, Levels.Info,
              ".reverse.head can be replaced with last: " + tree.toString().take(500), ReverseHead.this)
          case _ => continue(tree)
        }
      }
    }
  }
}