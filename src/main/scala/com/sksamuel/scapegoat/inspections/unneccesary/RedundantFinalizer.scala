package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel
  *
  *         Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#FI_USELESS
  * */
class RedundantFinalizer extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case dd@DefDef(mods, name, _, _, tpt, _)
          if mods.hasFlag(Flag.OVERRIDE) && name.toString == "finalize" && tpt.toString() == "Unit" =>
          feedback.warn("Redundant finalizer", tree.pos, Levels.Warning, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
