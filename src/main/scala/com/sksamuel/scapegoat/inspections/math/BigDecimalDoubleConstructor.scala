package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class BigDecimalDoubleConstructor
    extends Inspection(
      text = "Big decimal double constructor",
      defaultLevel = Levels.Warning,
      description = "Checks for use of BigDecimal(double) which can be unsafe.",
      explanation =
        "The results of this constructor can be somewhat unpredictable. E.g. writing new BigDecimal(0.1) in Java creates a BigDecimal which is actually equal to 0.1000000000000000055511151231257827021181583404541015625. This is because 0.1 cannot be represented exactly as a double."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._
          import definitions.{DoubleClass, FloatClass}

          private def isBigDecimal(pack: Tree) =
            pack.toString == "scala.`package`.BigDecimal" || pack.toString == "java.math.BigDecimal"

          private def isFloatingPointType(tree: Tree) =
            tree.tpe <:< FloatClass.tpe || tree.tpe <:< DoubleClass.tpe

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(pack, TermName("apply")), arg :: _)
                  if isBigDecimal(pack) && isFloatingPointType(arg) =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case Apply(Select(New(pack), nme.CONSTRUCTOR), arg :: _)
                  if isBigDecimal(pack) && isFloatingPointType(arg) =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
