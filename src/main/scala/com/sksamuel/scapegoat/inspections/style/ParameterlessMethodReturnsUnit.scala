package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class ParameterlessMethodReturnsUnit
    extends Inspection(
      text = "Parameterless methods returns unit",
      defaultLevel = Levels.Warning,
      description = "Checks for methods returning Unit that are defined without empty parentheses.",
      explanation =
        "Methods should be defined with empty parentheses if they have side effects. A method returning Unit must have side effects, therefore you should declare it with ()."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(_, _, _, vparamss, tpt, _) if tpt.tpe.toString == "Unit" && vparamss.isEmpty =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
