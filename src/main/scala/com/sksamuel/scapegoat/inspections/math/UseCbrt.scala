package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/**
 * @author
 *   Matic Potočnik
 */
class UseCbrt
    extends Inspection(
      text = "Use cbrt",
      defaultLevel = Levels.Info,
      description = "Checks for use of math.pow for calculating math.cbrt.",
      explanation = "Use math.cbrt, which is clearer and more performant than math.pow(x, 1/3)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(pack, TermName("pow")), List(_, Literal(Constant(third: Double))))
                  if (pack.symbol.fullNameString == "scala.math.package" || pack.symbol.fullNameString == "java.lang.Math" || pack.symbol.fullNameString == "java.lang.StrictMath")
                    && third >= 0.3333332
                    && third <= 0.3333334 =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
