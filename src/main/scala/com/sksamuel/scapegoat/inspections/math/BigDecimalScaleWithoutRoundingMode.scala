package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class BigDecimalScaleWithoutRoundingMode
    extends Inspection(
      text = "BigDecimal `setScale()` without rounding mode",
      defaultLevel = Levels.Warning,
      description =
        "Checks for use of `setScale()` on a BigDecimal without setting the rounding mode can throw an exception.",
      explanation =
        "When using `setScale()` on a BigDecimal without setting the rounding mode, this can throw an exception if rounding is required. Did you mean to call `setScale(s, RoundingMode.XYZ)`?"
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def isBigDecimal(t: Tree) =
            t.tpe <:< typeOf[BigDecimal] || t.tpe <:< typeOf[java.math.BigDecimal]

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(lhs, TermName("setScale")), List(_)) if isBigDecimal(lhs) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
