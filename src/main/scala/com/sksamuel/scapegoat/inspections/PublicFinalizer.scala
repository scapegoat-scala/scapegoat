package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector}

/** @author Stephen Samuel */
class PublicFinalizer extends Inspection {

  override def inspector(context: InspectionContext): Inspector = new Inspector(context) {

    import context.global._

    override def traverser = new context.Traverser {

      override def inspect(tree: Tree): Unit = {
        tree match {
          case _ => continue(tree)
        }
      }
    }
  }
}
