package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class CollectionNegativeIndex
    extends Inspection(
      text = "Collection index out of bounds",
      defaultLevel = Levels.Warning,
      description = "Checks for negative access on a sequence, e.g. list.get(-1).",
      explanation =
        "Trying to access Seq elements using a negative index will result in an IndexOutOfBoundsException."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(lhs, TermName("apply")), List(Literal(Constant(x: Int))))
                  if isList(lhs) && x < 0 =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
