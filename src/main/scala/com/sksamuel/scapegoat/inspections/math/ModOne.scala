package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 *
 * Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#INT_BAD_REM_BY_1
 */
class ModOne
    extends Inspection(
      text = "Integer mod one",
      defaultLevel = Levels.Warning,
      description = "Checks for expressions like x % 1.",
      explanation = "Any expression x % 1 will always return 0."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(lhs, TermName("$percent")), List(Literal(Constant(1))))
                  if lhs.tpe <:< typeOf[Int] =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
