package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class FindIsDefined extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(Apply(Select(_, TermName("find")), _), TermName("isDefined")) =>
          feedback.warn("use exists() not find().isDefined()", tree.pos, Levels.Info,
            ".find(x => Bool).isDefined can be replaced with exists(x => Bool): " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
