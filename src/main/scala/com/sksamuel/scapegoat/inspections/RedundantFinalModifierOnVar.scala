package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

class RedundantFinalModifierOnVar
    extends Inspection(
      text = "Redundant final modifier on a var",
      defaultLevel = Levels.Info,
      description = "Checks for redundant final modifier on vars.",
      explanation = "A final modifier on a var that cannot be overridden is redundant."
    ) {

  override def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {

      import context.global._

      override def postTyperTraverser =
        new context.Traverser {

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ValDef(mods, _, _, _)
                  if mods.isFinal && mods.isMutable &&
                    (tree.symbol.enclClass.isFinal || tree.symbol.enclClass.isCase || tree.symbol.enclClass.isModuleOrModuleClass || tree.symbol.enclClass.isPackageObjectOrClass) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
