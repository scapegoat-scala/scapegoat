package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class MaxParameters
    extends Inspection(
      text = "Max parameters",
      defaultLevel = Levels.Info,
      description = "Checks for methods that have over 10 parameters.",
      explanation =
        "Methods having a large number of parameters are more difficult to reason about, consider refactoring this code."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def count(vparamss: List[List[ValDef]]): Int =
            vparamss.foldLeft(0)((a, b) => a + b.size)

          private def countExceeds(vparamss: List[List[ValDef]], limit: Int): Boolean =
            count(vparamss) > limit

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(_, name, _, _, _, _) if name == nme.CONSTRUCTOR =>
              case DefDef(mods, _, _, _, _, _) if mods.isSynthetic        =>
              case DefDef(_, _, _, vparamss, _, _) if countExceeds(vparamss, 10) =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
