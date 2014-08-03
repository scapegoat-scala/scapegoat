package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Levels, Inspector, InspectionContext, Inspection}

/** @author Stephen Samuel */
class ZeroNumerator extends Inspection {
  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._
      import definitions._

      private def isFloatingPointType(tree: Tree) = tree.tpe <:< FloatClass.tpe || tree.tpe <:< DoubleClass.tpe
      private def isIntegralType(tree: Tree) = tree.tpe <:< IntClass.tpe || tree.tpe <:< LongClass.tpe

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("$div")), args) if isFloatingPointType(lhs) || isIntegralType(lhs) =>
            context.warn("Zero numerator", tree.pos, Levels.Warning, "Dividing zero by any number will always return zero")
          case _ => continue(tree)
        }
      }
    }
  }
}
