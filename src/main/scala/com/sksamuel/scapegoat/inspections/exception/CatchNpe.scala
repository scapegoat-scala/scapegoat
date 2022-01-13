package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class CatchNpe
    extends Inspection(
      text = "Catching NPE",
      defaultLevel = Levels.Error,
      description = "Checks for try blocks that catch null pointer exceptions.",
      explanation =
        "Avoid using null at all cost and you shouldn't need to catch NullPointerExceptions. Prefer Option to indicate potentially missing values and use Try to materialize exceptions thrown by any external libraries."
    ) {
  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def catchesNpe(cases: List[CaseDef]): Boolean =
            cases.exists(_.pat.tpe.toString == "NullPointerException")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Try(_, catches, _) if catchesNpe(catches) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
