package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyMethod extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(mods, _, _, _, _, Literal(Constant(()))) if !mods.hasFlag(Flag.SYNTHETIC) =>
            context.warn("Empty method", tree.pos, Levels.Warning, "Empty if statement " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
