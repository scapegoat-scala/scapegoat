package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class ReverseTailReverse
    extends Inspection(
      text = "reverse.tail.reverse instead of init",
      defaultLevel = Levels.Info,
      description = "Checks for use of reverse.tail.reverse.",
      explanation = "`reverse.tail.reverse` can be replaced with `init`, which is more concise."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Select(Select(c, TermName("reverse")), TermName("tail")), TermName("reverse"))
                  if isIterable(c) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case Select(
                    Apply(
                      arrayOps0,
                      List(
                        Select(
                          Apply(arrayOps1, List(Select(Apply(arrayOps2, List(_)), TermName("reverse")))),
                          TermName("tail")
                        )
                      )
                    ),
                    TermName("reverse")
                  )
                  if arrayOps0.toString.contains("ArrayOps")
                    && arrayOps1.toString.contains("ArrayOps")
                    && arrayOps2.toString.contains("ArrayOps") =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
