package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class UnnecessaryIf
    extends Inspection(
      text = "Unnecessary if condition.",
      defaultLevel = Levels.Info,
      description = "Checks for code like if (expr) true else false.",
      explanation =
        "If comparison is not needed. Use the condition, e.g. instead of if (a == b) true else false, use a == b or instead of if (a == b) false else true, use !(a == b)."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case If(_, Literal(Constant(true)), Literal(Constant(false))) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case If(_, Literal(Constant(false)), Literal(Constant(true))) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
