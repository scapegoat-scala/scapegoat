package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class ArraysInFormat extends Inspection("Array passed to String.format", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def containsArrayType(trees: List[Tree]) = trees.exists(isArray)

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(_, TermName("format")), args) if containsArrayType(args) =>
            context.warn(tree.pos, self, tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
