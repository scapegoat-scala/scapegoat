package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class VarUse extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case ValDef(modifiers, name, tpt, rhs) if modifiers.hasFlag(Flag.SYNTHETIC) =>
        case ValDef(modifiers, name, tpt, rhs) if modifiers.hasFlag(Flag.MUTABLE) =>
          feedback.warn("Use of var", tree.pos, Levels.Warning, "var used: " + tree.toString().take(300))
        case _ => super.traverse(tree)
      }
    }
  }
}
