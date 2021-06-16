package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class ReverseTakeReverse
    extends Inspection(
      text = "reverse.take().reverse instead of takeRight",
      defaultLevel = Levels.Info,
      description = "Checks for use of reverse.take().reverse.",
      explanation = "`reverse.take().reverse` can be replaced with `takeRight`, which is more concise."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(
                    Apply(Select(Select(c, TermName("reverse")), TermName("take")), _),
                    TermName("reverse")
                  ) if isIterable(c) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case Select(
                    Apply(
                      arrayOps0,
                      List(
                        Apply(
                          Select(
                            Apply(arrayOps1, List(Select(Apply(arrayOps2, List(_)), TermName("reverse")))),
                            TermName("take")
                          ),
                          _
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
