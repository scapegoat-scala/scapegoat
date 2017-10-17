package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyMethod extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          // its ok to do empty impl for overridden methods
          case DefDef(mods, _, _, _, _, _) if mods.isOverride =>
          case ClassDef(mods, _, _, _) if mods.isTrait => continue(tree)
          case DefDef(_, _, _, _, _, _) if tree.symbol != null && tree.symbol.enclClass.isTrait =>
          case DefDef(mods, _, _, _, _, Literal(Constant(()))) =>
            context.warn("Empty method", tree.pos, Levels.Warning, "Empty method statement " + tree.toString().take(500),
              EmptyMethod.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
