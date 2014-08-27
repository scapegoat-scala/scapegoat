package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyMethod extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          // its ok to do empty impl for overriden methods
          case DefDef(mods, _, _, _, _, _) if mods.isOverride =>
          case DefDef(mods, _, _, _, _, Literal(Constant(()))) if !mods.hasFlag(Flag.SYNTHETIC) =>
            context.warn("Empty method", tree.pos, Levels.Warning, "Empty method statement " + tree.toString().take(500),
              EmptyMethod.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
