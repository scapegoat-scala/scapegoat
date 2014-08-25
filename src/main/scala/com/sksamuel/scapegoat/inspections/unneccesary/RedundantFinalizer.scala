package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 *
 *         Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#FI_USELESS
 */
class RedundantFinalizer extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case dd @ DefDef(mods, name, _, _, tpt, _) if mods.hasFlag(Flag.OVERRIDE) && name.toString == "finalize" && tpt.toString() == "Unit" =>
            context.warn("Redundant finalizer", tree.pos, Levels.Warning, tree.toString().take(500), RedundantFinalizer.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
