package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class ParameterlessMethodReturnsUnit extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case d@DefDef(_, name, _, vparamss, tpt, _) if tpt.tpe.toString == "Unit" && vparamss.isEmpty =>
          feedback.warn("Parameterless methods returns unit", tree.pos, Levels.Warning, name.toString.take(300))
        case _ => super.traverse(tree)
      }
    }
  }
}
