package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

class FinalModifierOnCaseClass
    extends Inspection(
      text = "Missing final modifier on case class",
      defaultLevel = Levels.Info,
      description = "Checks for case classes without a final modifier.",
      explanation = "Using case classes without final modifier can lead to surprising breakage."
    ) {

  override def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {

      import context.global._

      override def postTyperTraverser =
        new context.Traverser {

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ClassDef(mods, _, _, _) if !mods.hasAbstractFlag && mods.isCase && !mods.isFinal =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
