package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

class UseLog10
    extends Inspection(
      text = "Use log10",
      defaultLevel = Levels.Info,
      description = "Checks for use of math.log(x)/math.log(10) instead of math.log10(x).",
      explanation = "Use math.log10(x), which is clearer and more performant than $math.log(x)/$math.log(10)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {

            def isMathPackage(pack: String) =
              pack == "scala.math.package" || pack == "java.lang.Math" || pack == "java.lang.StrictMath"

            tree match {
              case Apply(
                    Select(Apply(Select(pack1, TermName("log")), List(_)), nme.DIV),
                    List(Apply(Select(pack2, TermName("log")), List(Literal(Constant(10.0)))))
                  ) if isMathPackage(pack1.symbol.fullName) && isMathPackage(pack2.symbol.fullName) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
