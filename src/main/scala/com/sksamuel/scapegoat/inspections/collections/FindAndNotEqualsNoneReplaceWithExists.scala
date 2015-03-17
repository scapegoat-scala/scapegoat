package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

class FindAndNotEqualsNoneReplaceWithExists extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(_, TermName("find")), _), TermName("$bang$eq")),
            List(Select(_, TermName("None")))) =>
            context.warn("filter(_.isDefined).map(_.get)",
              tree.pos,
              Levels.Info,
              ".filter(_.isDefined).map(_.get) can be replaced with flatten: " + tree.toString().take(500),
              FindAndNotEqualsNoneReplaceWithExists.this)
          case _ => continue(tree)
        }
      }
    }
  }
}