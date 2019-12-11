package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyInterpolatedString extends Inspection("Empty interpolated string", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(_, TermName("apply")), List(_)), TermName("s")), Nil) =>
            context
              .warn(tree.pos, self,
                "String declared as interpolated but has no parameters: " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}