package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class ComparingFloatingPointTypes
    extends Inspection(
      text = "Floating type comparison",
      defaultLevel = Levels.Error,
      description = "Checks for equality checks on floating point types.",
      explanation =
        "Due to minor rounding errors, it is not advisable to compare floating-point numbers using the == operator. Either use a threshold based comparison, or switch to a BigDecimal."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val EqEq = TermName("$eq$eq")
          private val BangEq = TermName("$bang$eq")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(left, EqEq | BangEq), List(right)) =>
                val leftType = Option(left.tpe).map(_.typeSymbol).map(_.fullName).orNull
                val rightType = Option(right.tpe).map(_.typeSymbol).map(_.fullName).orNull
                val leftFloating = leftType == "scala.Double" || leftType == "scala.Float"
                val rightFloating = rightType == "scala.Double" || rightType == "scala.Float"
                if (leftFloating && rightFloating)
                  context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
