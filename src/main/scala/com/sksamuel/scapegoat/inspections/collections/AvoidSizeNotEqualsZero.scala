package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class AvoidSizeNotEqualsZero
    extends Inspection(
      text = "Avoid Traversable.size != 0",
      defaultLevel = Levels.Warning,
      description = "Checks for use of Traversable.size.",
      explanation = ".size can be slow for some data structures, prefer .nonEmpty, which is O(1)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Size = TermName("size")
          private val Length = TermName("length")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(
                    Select(Select(q, Length | Size), TermName("$bang$eq") | TermName("$greater")),
                    List(Literal(Constant(0)))
                  ) if isIterable(q) =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
