package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class ArraysInFormat
    extends Inspection(
      text = "Array passed to String.format",
      defaultLevel = Levels.Error,
      description = "Checks for arrays passed to String.format.",
      explanation = "An Array passed to String.format might result in an incorrect formatting."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def containsArrayType(trees: List[Tree]): Boolean = trees.exists(isArray)

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(_, TermName("format")), args) if containsArrayType(args) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
