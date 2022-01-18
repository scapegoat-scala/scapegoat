package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 *
 * Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#FI_USELESS
 */
class RedundantFinalizer
    extends Inspection(
      text = "Redundant finalizer",
      defaultLevel = Levels.Warning,
      description = "Checks for empty finalizers.",
      explanation =
        "An empty finalizer, e.g. override def finalize: Unit = {} is redundant and should be removed."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(mods, name, _, _, tpt, _)
                  if mods.hasFlag(Flag.OVERRIDE) && name.toString == "finalize" && tpt.toString() == "Unit" =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
