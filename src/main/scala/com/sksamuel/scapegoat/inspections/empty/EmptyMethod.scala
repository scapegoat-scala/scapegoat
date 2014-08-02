package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class EmptyMethod extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case DefDef(mods, _, _, _, _, Literal(Constant(()))) if !mods.hasFlag(Flag.SYNTHETIC) =>
          feedback.warn("Empty method", tree.pos, Levels.Warning, "Empty if statement " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
