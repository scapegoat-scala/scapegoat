package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class ZeroNumerator
    extends Inspection(
      text = "Zero numerator",
      defaultLevel = Levels.Warning,
      description = "Checks for dividing 0 by a number.",
      explanation = "Dividing zero by any number will always return zero, e.g. 0 / x == 0."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(Literal(Constant(0)), TermName("$div")), _) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
