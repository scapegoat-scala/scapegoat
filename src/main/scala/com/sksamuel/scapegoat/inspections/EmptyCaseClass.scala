package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class EmptyCaseClass extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          // body should have constructor only, and with synthetic methods it has 10 in total
          case ClassDef(mods, _, List(), Template(_, _, body))
            if mods.isCase && body.size == 10 =>
            context.warn("Empty case class", tree.pos, Levels.Info,
              "Empty case class can be rewritten as a case object",
              EmptyCaseClass.this)
          case _ => continue(tree)
        }
      }
    }
  }
}

