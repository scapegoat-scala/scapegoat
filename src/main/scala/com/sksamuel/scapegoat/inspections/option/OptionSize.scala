package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class OptionSize
    extends Inspection(
      text = "Prefer Option.isDefined instead of Option.size",
      defaultLevel = Levels.Error,
      description = "Checks for use of Option.size.",
      explanation =
        "Prefer to use Option.isDefined, Option.isEmpty or Option.nonEmpty instead of Option.size."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(Apply(option2Iterable, List(_)), TermName("size")) =>
                if (option2Iterable.symbol.fullName == "scala.Option.option2Iterable")
                  context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
