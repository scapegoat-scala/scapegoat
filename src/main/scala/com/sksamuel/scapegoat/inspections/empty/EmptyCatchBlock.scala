package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class EmptyCatchBlock extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    def checkCatch(cd: CaseDef): Unit = {
      if (cd.body.toString == "()")
        feedback.warn("Empty finalizer", cd.pos, Levels.Warning,
          "Empty finalizer near " + cd.toString().take(100))
    }

    def checkCatches(defs: List[CaseDef]) = defs.foreach(cd => checkCatch(cd))

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Try(body, catches, finalizer) => checkCatches(catches)
        case _ => super.traverse(tree)
      }
    }
  }
}
