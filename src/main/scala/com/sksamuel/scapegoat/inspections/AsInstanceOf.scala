package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat._

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class AsInstanceOf extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case TypeApply(Select(_, TermName("asInstanceOf")), _) =>
          feedback.warn("Use of asInstanceOf", tree.pos, Levels.Warning,
            "asInstanceOf used near " + tree.toString().take(500) + ". Consider using pattern matching.")
        case DefDef(modifiers, _, _, _, _, _) if modifiers.hasFlag(Flag.SYNTHETIC) => // no further
        case m@Match(selector, cases) => // ignore selector and process cases
          cases.foreach(traverse)
        case _ => super.traverse(tree)
      }
    }
  }
}
