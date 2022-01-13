package com.sksamuel.scapegoat.inspections.string

import java.util.regex.PatternSyntaxException

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class InvalidRegex
    extends Inspection(
      text = "Invalid regex",
      defaultLevel = Levels.Info,
      description = "Checks for invalid regex literals.",
      explanation =
        "Invalid regex literals can fail at compile time with a PatternSyntaxException. This could be caused by e.g. dangling meta characters, or unclosed escape characters, etc."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(
                    Apply(Select(_, TermName("augmentString")), List(Literal(Constant(regex)))),
                    TermName("r")
                  ) =>
                try regex.toString.r
                catch {
                  case _: PatternSyntaxException =>
                    context.warn(tree.pos, self)
                }
              case _ => continue(tree)
            }
          }
        }
    }
}
