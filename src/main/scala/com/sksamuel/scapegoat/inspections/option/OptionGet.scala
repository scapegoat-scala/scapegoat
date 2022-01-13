package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class OptionGet
    extends Inspection(
      text = "Use of Option.get",
      defaultLevel = Levels.Error,
      description = "Checks for use of Option.get.",
      explanation =
        "Using Option.get defeats the purpose of using Option in the first place. Use the following instead: Option.getOrElse, Option.fold, pattern matching or don't take the value out of the container and map over it to transform it."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(left, TermName("get")) =>
                if (left.tpe.typeSymbol.fullName == "scala.Option")
                  context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
