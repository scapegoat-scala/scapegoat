package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class VarUse extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def traverse(tree: Tree): Unit = {
        tree match {
          case ValDef(modifiers, name, tpt, rhs) if modifiers.hasFlag(Flag.SYNTHETIC) =>
          case ValDef(modifiers, name, tpt, rhs) if modifiers.hasFlag(Flag.MUTABLE) =>
            context.warn("Use of var", tree.pos, Levels.Warning, "var used: " + tree.toString().take(300))
          case _ => super.traverse(tree)
        }
      }
    }
  }
}
