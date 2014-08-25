package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class NanComparison extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._
      import definitions._

      private def isNan(value: Any): Boolean = {
        value match {
          case d: Double => d.isNaN
          case _         => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("$eq$eq")), List(Literal(Constant(x)))) if isFloatingPointType(lhs) && isNan(x) =>
            warn(tree)
          case Apply(Select(Literal(Constant(x)), TermName("$eq$eq")), List(rhs)) if isFloatingPointType(rhs) && isNan(x) =>
            warn(tree)
          case _ => continue(tree)
        }
      }

      private def isFloatingPointType(lhs: Tree): Boolean = {
        lhs.tpe <:< DoubleClass.tpe || lhs.tpe <:< FloatClass.tpe
      }

      private def warn(tree: Tree) {
        context.warn("Nan comparision", tree.pos, Levels.Error,
          "NaN comparision will always fail. Use value.isNan instead.",
          NanComparison.this)
      }
    }
  }
}