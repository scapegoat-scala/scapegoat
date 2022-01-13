package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class UnnecessaryReturnUse
    extends Inspection(
      text = "Unnecessary return",
      defaultLevel = Levels.Info,
      description = "Checks for use of return keyword in blocks.",
      explanation =
        "Scala returns the value of the last expression in a block. Use of return here is not an idiomatic Scala."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(_, _, _, _, _, Block(_, Return(_))) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
