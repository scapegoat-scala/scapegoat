package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/**
 * @author
 *   Matic Potočnik
 */
class UseExpM1
    extends Inspection(
      text = "Use expm1",
      defaultLevel = Levels.Info,
      description = "Checks for use of math.exp(x) - 1 instead of math.expm1(x).",
      explanation = "Use math.expm1(x), which is clearer and more performant than math.exp(x) - 1."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(
                    Select(Apply(Select(_, TermName("exp")), List(_)), nme.SUB),
                    List(Literal(Constant(1)))
                  ) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
