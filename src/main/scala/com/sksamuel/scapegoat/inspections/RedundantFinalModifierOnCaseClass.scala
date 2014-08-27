package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

class RedundantFinalModifierOnCaseClass extends Inspection {

  override def inspector(context: InspectionContext): Inspector = new Inspector(context) {

    import context.global._

    override def postTyperTraverser = Some apply new context.Traverser {

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ClassDef(mods, _, _, _) if mods.isCase && mods.isFinal =>
            context.warn("Redundant final modifier on case class",
              tree.pos,
              Levels.Info,
              "Case classes cannot be extended, final modifer is redundant",
              RedundantFinalModifierOnCaseClass.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
