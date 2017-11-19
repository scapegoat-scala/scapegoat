package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class FilterOptionAndGet extends Inspection(
  "filter(_.isDefined).map(_.get) instead of flatten", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(
            Select(Apply(Select(_, TermName("filter")), List(Function(_, Select(_, TermName("isDefined"))))),
              TermName("map")), args), List(Function(_, Select(_, TermName("get"))))) =>
            context.warn(tree.pos, self,
              ".filter(_.isDefined).map(_.get) can be replaced with flatten: " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}