package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat._

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class IsInstanceOf extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case TypeApply(Select(_, TermName("isInstanceOf")), _) =>
          feedback.warn("Use of isInstanceOf", tree.pos, Levels.Warning,
            "Consider using a pattern match rather than isInstanceOf: " + tree.toString().take(500))
        case DefDef(modifiers, _, _, _, _, _) if modifiers.hasFlag(Flag.SYNTHETIC) => // avoid partial function stuff
        case m@Match(selector, cases) => // ignore selector and process cases
          cases.foreach(traverse)
        case _ => super.traverse(tree)
      }
    }
  }
}
