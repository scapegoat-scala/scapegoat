package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class EmptyInterpolatedString
    extends Inspection(
      text = "Empty interpolated string",
      defaultLevel = Levels.Error,
      description = "Looks for interpolated strings that have no arguments.",
      explanation =
        "String declared as interpolated but has no parameters can be turned into a regular string."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(Apply(Select(_, TermName("apply")), List(_)), TermName("s")), Nil) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
