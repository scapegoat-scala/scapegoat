package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class NegativeSeqPad
    extends Inspection(
      text = "Negative seq padTo",
      defaultLevel = Levels.Error,
      description = "Checks for use of padTo with negative length.",
      explanation = "Seq.padTo with a negative length will not have any effect."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(TypeApply(Select(_, TermName("padTo")), _), Literal(Constant(_)) :: _) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
