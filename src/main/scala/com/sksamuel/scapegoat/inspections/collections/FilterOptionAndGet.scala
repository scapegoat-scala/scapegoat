package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class FilterOptionAndGet extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(TypeApply(
        Select(Apply(Select(_, TermName("filter")), List(Function(_, Select(_, TermName("isDefined"))))),
        TermName("map")), args), List(Function(_, Select(_, TermName("get"))))) =>
          feedback.warn("filter(_.isDefined).map(_.get)", tree.pos, Levels.Info,
            ".filter(_.isDefined).map(_.get) can be replaced with flatten: " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}