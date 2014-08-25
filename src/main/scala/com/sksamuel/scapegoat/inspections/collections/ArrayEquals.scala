package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class ArrayEquals extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def isArray(tree: Tree) = tree.tpe <:< typeOf[Array[_]]

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("$eq$eq")), List(rhs)) if isArray(lhs) && isArray(rhs) =>
            context.warn("Array equals",
              tree.pos,
              Levels.Info,
              "Array equals is not an equality check. Use a.deep == b.deep or convert to another collection type",
              ArrayEquals.this)
          case _ => continue(tree)
        }
      }
    }
  }
}