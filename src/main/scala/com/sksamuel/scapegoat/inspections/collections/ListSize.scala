package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class ListSize
    extends Inspection(
      text = "List.size is O(n)",
      defaultLevel = Levels.Info,
      description = "Checks for use of List.size.",
      explanation =
        "List.size is O(n). Consider using a different data type with O(1) size lookup such as Vector or an Array."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(lhs, TermName("size")) if isList(lhs) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
