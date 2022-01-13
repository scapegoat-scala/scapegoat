package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class ListAppend
    extends Inspection(
      text = "List append is slow",
      defaultLevel = Levels.Info,
      description = "Checks for when elements are appended to a list.",
      explanation =
        "List append is O(n). For large lists, consider using cons (::) or another data structure such as ListBuffer, Vector or a cats.data.Chain (which has constant prepend and append)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Append = TermName("$colon$plus")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(TypeApply(Select(lhs, Append), _), _) if isList(lhs) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
