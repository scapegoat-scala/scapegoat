package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class ReverseFunc
    extends Inspection(
      text = "Unnecessary reverse",
      defaultLevel = Levels.Info,
      description = "Checks for use of reverse followed by head/headOption/iterator/map.",
      explanation =
        "`reverse` followed by `head`, `headOption`, `iterator`, or `map` can be replaced, respectively, with `last`, `lastOption`, `reverseIterator`, or `reverseMap`."
    ) {

  object FuncReplace {

    private val funcReplace = Map(
      "head" -> "last",
      "headOption" -> "lastOption",
      "iterator" -> "reverseIterator",
      "map" -> "reverseMap"
    )

    def unapply(func: String): Option[(String, String)] =
      funcReplace.find(_._1 == func)
  }

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Select(c, TermName("reverse")), TermName(FuncReplace(_, _)))
                  if c.tpe <:< typeOf[Iterable[Any]] =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case Select(
                    Apply(arrayOps1, List(Select(Apply(arrayOps2, List(_)), TermName("reverse")))),
                    TermName(FuncReplace(_, _))
                  ) if arrayOps1.toString.contains("ArrayOps") && arrayOps2.toString.contains("ArrayOps") =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
