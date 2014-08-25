package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class MaxParameters extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def count(vparamss: List[List[ValDef]]) = vparamss.foldLeft(0)((a, b) => a + b.size) > 10

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(_, name, _, _, _, _) if name == nme.CONSTRUCTOR =>
          case DefDef(mods, _, _, _, _, _) if mods.isSynthetic        =>
          case DefDef(_, name, _, vparamss, _, _) if count(vparamss) =>
            context.warn("Max parameters",
              tree.pos,
              Levels.Info,
              s"Method $name has ${count(vparamss)} parameters. Consider refactoring to a containing instance.",
              MaxParameters.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
