package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class BigDecimalScaleWithoutRoundingMode extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def isBigDecimal(t: Tree) = t.tpe <:< typeOf[BigDecimal] || t.tpe <:< typeOf[java.math.BigDecimal]

      private def warn(tree: Tree): Unit = {
        context.warn("BigDecimal setScale() without rounding mode",
          tree.pos,
          Levels.Warning,
          "When using setScale() on a BigDecimal without setting the rounding mode, this can throw an exception if rounding is required. Did you mean to call setScale(s, RoundingMode.XYZ)",
          BigDecimalScaleWithoutRoundingMode.this)
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("setScale")), List(arg)) if isBigDecimal(lhs) => warn(tree)
          case _ => continue(tree)
        }
      }
    }
  }
}