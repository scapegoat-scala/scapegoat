package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class ArraysInFormat extends Inspection("Incorrect number of args for format", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def containsArrayType(trees: List[Tree]) = trees.exists(_.tpe <:< typeOf[Array[_]])

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("format")), args) if containsArrayType(args) =>
            context.warn(tree.pos, self, tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
