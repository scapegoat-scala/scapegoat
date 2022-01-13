package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class DuplicateSetValue
    extends Inspection(
      text = "Duplicated set value",
      defaultLevel = Levels.Warning,
      description = "Checks for duplicate values in set literals.",
      explanation = "A set value is overwritten by a later entry."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def hasDuplicates(trees: List[Tree]): Boolean = {
            val values: Set[Any] = trees.map {
              case Literal(Constant(x)) => x
              case x                    => x
            }.toSet
            values.size < trees.size
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(TypeApply(Select(Select(_, TermName("Set")), TermName("apply")), _), args)
                  if hasDuplicates(args) =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
