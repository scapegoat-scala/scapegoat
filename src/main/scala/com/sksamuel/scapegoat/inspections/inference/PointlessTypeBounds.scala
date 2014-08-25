package com.sksamuel.scapegoat.inspections.inference

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class PointlessTypeBounds extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case t @ TypeDef(_, _, _, rhs) if rhs.tpe.bounds.isEmptyBounds
            && rhs.pos != null
            && (rhs.pos.lineContent.contains("<: Any") || rhs.pos.lineContent.contains(">: Nothing")) =>
            context.warn("Pointless Type Bounds",
              tree.pos,
              Levels.Warning,
              "Type bound resolves to Nothing <: T <: Any. Did you mean to put in other bounds: " +
                tree.toString().take(300),
              PointlessTypeBounds.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
