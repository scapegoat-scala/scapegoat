package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class ArraysInFormat extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def containsArrayType(trees: List[Tree]) = trees.exists(_.tpe <:< typeOf[Array[_]])

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("format")), args) if containsArrayType(args) =>
            context.warn("Incorrect number of args for format",
              tree.pos,
              Levels.Error,
              tree.toString().take(500),
              ArraysInFormat.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
