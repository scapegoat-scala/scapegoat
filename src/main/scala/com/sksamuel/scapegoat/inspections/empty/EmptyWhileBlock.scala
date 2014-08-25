package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class EmptyWhileBlock extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case LabelDef(_, _, If(_, Block(List(Literal(Constant(()))), _), _)) =>
            context.warn("Empty while block", tree.pos, Levels.Warning, tree.toString().take(500), EmptyWhileBlock.this)
          case _ => continue(tree)
        }
      }
    }
  }
}