package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 *
 * Checks for if statements where the condition evaluates to a constant true or a constant false.
 */
class ConstantIf
    extends Inspection(
      text = "Constant if expression",
      defaultLevel = Levels.Warning,
      description = "Checks for code where the if condition compiles to a constant.",
      explanation =
        "An if condition which gets compiled to a constant, like e.g. if (1 < 2) or if (false) doesn't add any value and should be avoided."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              // ignore while loops, this will be picked up by the WhileTrue inspection
              case LabelDef(_, _, _) =>
              case If(cond, _, _) =>
                if (cond.toString() == "false" || cond.toString() == "true")
                  context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
