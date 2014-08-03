package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyCatchBlock extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      def checkCatch(cd: CaseDef): Unit = {
        if (cd.body.toString == "()")
          context.warn("Empty catch block", cd.pos, Levels.Warning,
            "Empty catch block " + cd.toString().take(100), EmptyCatchBlock.this)
      }

      def checkCatches(defs: List[CaseDef]) = defs.foreach(cd => checkCatch(cd))

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(body, catches, finalizer) => checkCatches(catches)
          case _ => continue(tree)
        }
      }
    }
  }
}