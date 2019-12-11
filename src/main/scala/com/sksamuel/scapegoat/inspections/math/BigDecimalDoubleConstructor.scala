package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class BigDecimalDoubleConstructor extends Inspection("Big decimal double constructor", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._
      import definitions.{DoubleClass, FloatClass}

      private def isBigDecimal(pack: Tree) =
        pack.toString == "scala.`package`.BigDecimal" || pack.toString == "java.math.BigDecimal"
      private def isFloatingPointType(tree: Tree) = tree.tpe <:< FloatClass.tpe || tree.tpe <:< DoubleClass.tpe

      private def warn(tree: Tree): Unit = {
        context.warn(tree.pos, self,
          "The results of this constructor can be somewhat unpredictable. " +
            "Eg, writing new BigDecimal(0.1) in Java creates a BigDecimal which is actually equal to 0.1000000000000000055511151231257827021181583404541015625. " +
            "This is because 0.1 cannot be represented exactly as a double. " + tree.toString().take(100))
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(pack, TermName("apply")), arg :: _) if isBigDecimal(pack) && isFloatingPointType(arg) =>
            warn(tree)
          case Apply(Select(New(pack), nme.CONSTRUCTOR), arg :: _) if isBigDecimal(pack) && isFloatingPointType(arg) =>
            warn(tree)
          case _ => continue(tree)
        }
      }
    }
  }
}