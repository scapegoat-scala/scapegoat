package com.sksamuel.scapegoat.inspections.akka

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector}

/** @author Stephen Samuel */
class AkkaSenderClosure extends Inspection {

  override def inspector(context: InspectionContext): Inspector = new Inspector(context) {

    import context.global._

    override def traverser = new context.Traverser {

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Function(List(ValDef(_, _, _, _)), body) =>
          case _ => continue(tree)
        }
      }
    }
  }
}
