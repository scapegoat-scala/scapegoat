package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class UnnecessaryIf extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case If(cond, Literal(Constant(true)), Literal(Constant(false))) =>
          feedback.warn("Unncessary if condition.", tree.pos, Levels.Info, "If comparision is not needed. Use the condition. Eg, instead of if (a ==b) true else false, simply use a == b. : "+ tree.toString().take(500))
        case If(cond, Literal(Constant(false)), Literal(Constant(true))) =>
          feedback.warn("Unncessary if condition.", tree.pos, Levels.Info, "If comparision is not needed. Use the negated condition. Eg, instead of if (a ==b) false else true, simply use !(a == b). : "+ tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
