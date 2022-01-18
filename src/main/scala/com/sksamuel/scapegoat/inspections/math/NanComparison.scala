package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class NanComparison
    extends Inspection(
      text = "Nan comparison",
      defaultLevel = Levels.Error,
      description = "Checks for x == Double.NaN which will always fail.",
      explanation = "NaN comparison will always fail. Use value.isNan instead."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

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
              case Apply(Select(lhs, TermName("$eq$eq")), List(Literal(Constant(x))))
                  if isFloatingPointType(lhs) && isNan(x) =>
                context.warn(tree.pos, self)
              case Apply(Select(Literal(Constant(x)), TermName("$eq$eq")), List(rhs))
                  if isFloatingPointType(rhs) && isNan(x) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }

          private def isFloatingPointType(lhs: Tree): Boolean =
            lhs.tpe <:< DoubleClass.tpe || lhs.tpe <:< FloatClass.tpe
        }
    }
}
