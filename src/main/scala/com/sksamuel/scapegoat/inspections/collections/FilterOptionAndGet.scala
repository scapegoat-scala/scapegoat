package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class FilterOptionAndGet
    extends Inspection(
      text = "filter(_.isDefined).map(_.get) instead of flatten",
      defaultLevel = Levels.Info,
      description = "Checks whether the expression can be rewritten using flatten.",
      explanation = "`filter(_.isDefined).map(_.get)` can be replaced with `flatten`."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(
                    TypeApply(
                      Select(
                        Apply(
                          Select(_, TermName("filter")),
                          List(Function(_, Select(_, TermName("isDefined"))))
                        ),
                        TermName("map")
                      ),
                      _
                    ),
                    List(Function(_, Select(_, TermName("get"))))
                  ) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
