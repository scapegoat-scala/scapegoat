package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EmptyCatchBlock extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      def checkCatch(cd: CaseDef): Unit = {
        if (cd.body.toString == "()")
          context.warn("Empty finalizer", cd.pos, Levels.Warning,
            "Empty finalizer near " + cd.toString().take(100))
      }

      def checkCatches(defs: List[CaseDef]) = defs.foreach(cd => checkCatch(cd))

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(body, catches, finalizer) => checkCatches(catches)
          case _ => super.traverse(tree)
        }
      }
    }
  }
}