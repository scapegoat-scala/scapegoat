package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{ Levels, Inspection, InspectionContext, Inspector }

/** @author Stephen Samuel */
class EmptyFor extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Foreach = TermName("foreach")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(_, Foreach), _), List(Function(List(ValDef(mods, _, _, EmptyTree)), Literal(Constant(()))))) =>
            context.warn("Empty for loop", tree.pos, Levels.Warning, tree.toString().take(500), EmptyFor.this)
          case _ => continue(tree)
        }
      }
    }
  }
}

