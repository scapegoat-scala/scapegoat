package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class EitherGet extends Inspection("Use of Either Right or Left Projection get", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(_, TermName("right")), TermName("get")) =>
            context.warn(tree.pos, self, "Use of Either Right Projection get: " + tree.toString().take(500))
          case Select(Select(_, TermName("left")), TermName("get")) =>
            context.warn(tree.pos, self, "Use of Either Left Projection get: " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}