package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyInterpolatedString extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(lhs, TermName("apply")), List(string)), TermName("s")), Nil) =>
            context
              .warn("Empty interpolated string",
                tree.pos,
                Levels.Error,
                "String declared as interpolated but has no parameters: " + tree.toString().take(500),
                EmptyInterpolatedString.this)
          case _ => continue(tree)
        }
      }
    }
  }
}