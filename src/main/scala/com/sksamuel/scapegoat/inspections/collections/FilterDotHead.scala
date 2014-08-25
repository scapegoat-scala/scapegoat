package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class FilterDotHead extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Filter = TermName("filter")
      private val Head = TermName("head")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Apply(Select(_, Filter), _), Head) =>
            context.warn("filter().head can throw an exception; use find()",
              tree.pos,
              Levels.Info,
              ".filter(x => Bool).head can be replaced with find(x => Bool) and a match: " + tree.toString().take(500),
              FilterDotHead.this)
          case _ => continue(tree)
        }
      }
    }
  }
}