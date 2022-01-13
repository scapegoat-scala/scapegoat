package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class ImpossibleOptionSizeCondition
    extends Inspection(
      text = "Impossible Option.size condition",
      defaultLevel = Levels.Error,
      description = "Checks for code like option.size > 1.",
      explanation = "Option.size > 1 can never be true, did you mean to use Option.nonEmpty instead?"
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Opt2Iterable = TermName("option2Iterable")
          private val Size = TermName("size")
          private val Greater = TermName("$greater")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(
                    Select(Select(Apply(TypeApply(Select(_, Opt2Iterable), _), _), Size), Greater),
                    List(Literal(Constant(x: Int)))
                  ) if x > 1 =>
                context.warn(tree.pos, self, tree.toString.take(200))
              case _ => continue(tree)
            }
          }
        }
    }

}
