package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class UnnecessaryReturnUse extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Block(_, Return(expr)) =>
          feedback.warn("Unnecessary return", tree.pos, Levels.Error,
            "Scala returns the value of the last expression in a function. Use of return is not needed here")
        case _ => super.traverse(tree)
      }
    }
  }
}
