package com.sksamuel.scapegoat.inspections.unsafe

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class TryGet
    extends Inspection(
      text = "Use of Try.get",
      defaultLevel = Levels.Error,
      description = "Checks for use of Try.get.",
      explanation =
        "Using Try.get is unsafe because it will throw the underlying exception in case of a Failure. Use the following instead: Try.getOrElse, Try.fold, pattern matching or don't take the value out of the container and map over it to transform it."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Select(left, TermName("get")) =>
                if (left.tpe.typeSymbol.fullName == "scala.util.Try")
                  context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
