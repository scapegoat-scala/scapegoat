package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class ReverseTakeReverse extends Inspection("reverse.take(...).reverse instead of takeRight", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Apply(Select(Select(c, TermName("reverse")), TermName("take")), _), TermName("reverse")) if isTraversable(c) =>
            warn(tree)
          case Select(Apply(arrayOps0, List(Apply(Select(Apply(arrayOps1, List(Select(Apply(arrayOps2, List(_)), TermName("reverse")))), TermName("take")), _))), TermName("reverse")) if (arrayOps0.toString.contains("ArrayOps"))
            && (arrayOps1.toString.contains("ArrayOps"))
            && (arrayOps2.toString.contains("ArrayOps")) =>
            warn(tree)
          case _ => continue(tree)
        }
      }

      private def warn(tree: Tree) = {
        context.warn(tree.pos, self,
          ".reverse.take(...).reverse can be replaced with takeRight: " + tree.toString().take(500))
      }
    }
  }
}