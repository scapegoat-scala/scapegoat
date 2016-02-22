package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class ReverseTailReverse extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(Select(_, TermName("reverse")), TermName("tail")), TermName("reverse")) =>
            context.warn("reverse.tail.reverse instead of init", tree.pos, Levels.Info,
              ".reverse.tail.reverse can be replaced with init: " + tree.toString().take(500), ReverseTailReverse.this)
          case _ => continue(tree)
        }
      }
    }
  }
}