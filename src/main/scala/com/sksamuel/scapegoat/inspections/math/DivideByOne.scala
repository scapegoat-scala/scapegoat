package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class DivideByOne
    extends Inspection(
      text = "Divide by one",
      defaultLevel = Levels.Warning,
      description = "Checks for division by one.",
      explanation = "Divide by one will always return the original value."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def isNumber(tree: Tree) = {
            tree.tpe <:< typeOf[Int] ||
            tree.tpe <:< typeOf[Long] ||
            tree.tpe <:< typeOf[Double] ||
            tree.tpe <:< typeOf[Float]
          }

          private def isOne(value: Any): Boolean =
            value match {
              case i: Int => i == 1
              case _      => false
            }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(lhs, TermName("$div")), List(Literal(Constant(x))))
                  if isNumber(lhs) && isOne(x) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
