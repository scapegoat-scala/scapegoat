package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class MaxParameters extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(_, name, _, _, _, _) if name == nme.CONSTRUCTOR =>
          case DefDef(mods, _, _, _, _, _) if mods.isSynthetic =>
          case DefDef(_, _, _, vparamss, _, _) if vparamss.foldLeft(0)((a, b) => a + b.size) > 10 =>
            context.warn("Max parameters",
              tree.pos,
              Levels.Info,
              "Method $name has ${vparams.size} parameters. Consider refactoring to a containing instance.",
              MaxParameters.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
