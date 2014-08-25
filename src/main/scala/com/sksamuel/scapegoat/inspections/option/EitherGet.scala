package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EitherGet extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(_, TermName("right")), TermName("get")) =>
            context.warn("Use of Either Right Projection get", tree.pos, Levels.Error, tree.toString().take(500), EitherGet.this)
          case Select(Select(_, TermName("left")), TermName("get")) =>
            context.warn("Use of Either Left Projection get", tree.pos, Levels.Error, tree.toString().take(500), EitherGet.this)
          case _ => continue(tree)
        }
      }
    }
  }
}