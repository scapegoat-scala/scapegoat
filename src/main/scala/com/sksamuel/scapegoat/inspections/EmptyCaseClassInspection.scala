package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class EmptyCaseClassInspection extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case ClassDef(mods, _, List(), Template(_, _, _)) if mods.isCase =>
            context.warn("Empty case class", tree.pos, Levels.Info,
              "Empty case class can be rewritten as a case object",
              EmptyCaseClassInspection.this)
          case _ => continue(tree)
        }
      }
    }
  }
}

