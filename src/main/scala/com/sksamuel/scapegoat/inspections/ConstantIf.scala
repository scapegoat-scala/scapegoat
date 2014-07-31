package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class ConstantIf extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case If(cond, thenp, elsep) =>
          if (cond.toString() == "false" || cond.toString() == "true")
            feedback.warn("Constant if expression", tree.pos, Levels.Warning,
              "Constant if expression " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
