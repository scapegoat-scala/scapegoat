package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{ Levels, Inspection, InspectionContext, Inspector }

/** @author Stephen Samuel */
class EmptyFor extends Inspection("Empty for loop", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Foreach = TermName("foreach")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(_, Foreach), _), List(Function(List(ValDef(mods, _, _, EmptyTree)), Literal(Constant(()))))) =>
            context.warn(tree.pos, self, tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}

