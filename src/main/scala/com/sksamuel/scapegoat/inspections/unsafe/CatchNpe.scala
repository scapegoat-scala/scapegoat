package com.sksamuel.scapegoat.inspections.unsafe

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class CatchNpe extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    private def catchesNpe(cases: List[CaseDef]): Boolean = {
      cases.exists(_.pat.tpe.toString == "NullPointerException")
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Try(block, catches, finalizer) if catchesNpe(catches) =>
          feedback.warn("Catching NPE", tree.pos, level = Levels.Error)
        case _ => super.traverse(tree)
      }
    }
  }
}
