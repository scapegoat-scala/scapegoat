package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class UnnecessaryOverride extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(mods, name, _, vparamss, _, Apply(Select(Super(This(_), _), name2), args)) if name == name2 && vparamss.foldLeft(0)((a, b) => a + b.size) == args.size =>
            context.warn("Unnecessary Override",
              tree.pos,
              Levels.Info,
              "This method is overriden yet only calls super: " + tree.toString().take(200),
              UnnecessaryOverride.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
