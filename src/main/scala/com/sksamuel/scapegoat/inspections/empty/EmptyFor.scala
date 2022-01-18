package com.sksamuel.scapegoat.inspections.empty

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class EmptyFor
    extends Inspection(
      text = "Empty for loop",
      defaultLevel = Levels.Warning,
      description = "Checks for empty for loops.",
      explanation = "An empty for loop isn't a common practice and in most cases is considered as dead code."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val Foreach = TermName("foreach")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(
                    TypeApply(Select(_, Foreach), _),
                    List(Function(List(ValDef(_, _, _, EmptyTree)), Literal(Constant(()))))
                  ) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
