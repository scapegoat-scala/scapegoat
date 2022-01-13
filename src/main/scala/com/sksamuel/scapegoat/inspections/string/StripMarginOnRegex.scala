package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class StripMarginOnRegex
    extends Inspection(
      text = "Strip margin on regex",
      defaultLevel = Levels.Error,
      description = "Checks for .stripMargin calls on regex strings that contain a pipe.",
      explanation = "Strip margin will strip | from regex - possible corrupted regex."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val R = TermName("r")
          private val StripMargin = TermName("stripMargin")
          private val Augment = TermName("augmentString")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(
                    Apply(
                      _,
                      List(
                        Select(Apply(Select(_, Augment), List(Literal(Constant(str: String)))), StripMargin)
                      )
                    ),
                    R
                  ) if str.contains('|') =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
