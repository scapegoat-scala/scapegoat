package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

class UseLog1P
    extends Inspection(
      text = "Use log1p",
      defaultLevel = Levels.Info,
      description = "Checks for use of math.log(x + 1) instead of math.log1p(x).",
      explanation = "Use math.log1p(x) is clearer and more performant than $math.log(1 + x)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          def isMathPackage(pack: String): Boolean =
            pack == "scala.math.package" || pack == "java.lang.Math" || pack == "java.lang.StrictMath"

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(pack, TermName("log")), List(Apply(Select(Literal(Constant(1)), nme.ADD), _)))
                  if isMathPackage(pack.symbol.fullName) =>
                context.warn(tree.pos, self)
              case Apply(
                    Select(pack, TermName("log")),
                    List(Apply(Select(_, nme.ADD), List(Literal(Constant(1)))))
                  ) if isMathPackage(pack.symbol.fullName) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
