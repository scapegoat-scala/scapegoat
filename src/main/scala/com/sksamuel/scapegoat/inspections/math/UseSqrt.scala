package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

class UseSqrt
    extends Inspection(
      text = "Use sqrt",
      defaultLevel = Levels.Info,
      description = "Checks for use of math.pow for calculating math.sqrt.",
      explanation = "Use math.sqrt, which is clearer and more performant than $math.pow(x, 0.5)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {

      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(pack, TermName("pow")), List(_, Literal(Constant(0.5d))))
                  if pack.symbol.fullNameString == "scala.math.package" ||
                    pack.symbol.fullNameString == "java.lang.StrictMath" ||
                    pack.symbol.fullNameString == "java.lang.Math" =>
                context.warn(tree.pos, self)
              case _ =>
                continue(tree)
            }
          }
        }
    }
}
