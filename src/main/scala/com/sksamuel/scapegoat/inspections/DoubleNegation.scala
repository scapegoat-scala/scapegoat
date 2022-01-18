package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class DoubleNegation
    extends Inspection(
      text = "Double negation",
      defaultLevel = Levels.Info,
      description = "Checks for code like !(!b).",
      explanation = "Double negation can be removed, e.g. !(!b) it equal to just b."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Bang = TermName("unary_$bang")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Select(_, Bang), Bang) =>
                context.warn(tree.pos, self, tree.toString.take(200))
              case _ => continue(tree)
            }
          }
        }
    }
}
