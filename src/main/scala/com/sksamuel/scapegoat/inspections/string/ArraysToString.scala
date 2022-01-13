package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class ArraysToString
    extends Inspection(
      text = "Use of Array.toString",
      defaultLevel = Levels.Warning,
      description = "Checks for explicit toString calls on arrays.",
      explanation = "Calling toString on an array does not perform a deep toString."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val ToString = TermName("toString")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(lhs, ToString), Nil) if isArray(lhs) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
