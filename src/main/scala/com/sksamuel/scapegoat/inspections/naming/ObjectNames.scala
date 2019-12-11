package com.sksamuel.scapegoat.inspections.naming

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class ObjectNames extends Inspection("Object name not recommended", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val regex = "^[A-Za-z0-9]*$"

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ModuleDef(mods, name, _) if !mods.isSynthetic && !name.toString.matches(regex) =>
            context.warn(tree.pos, self, s"Object names should only contain alphanum chars (bad = $name)")
          case _ => continue(tree)
        }
      }
    }
  }
}
