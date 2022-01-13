package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class PreferSetEmpty
    extends Inspection(
      text = "Prefer Set.empty",
      defaultLevel = Levels.Info,
      description = "Checks for use of Set().",
      explanation =
        "`Set[T]()` allocates an intermediate object. Consider `Set.empty` which returns a singleton instance without creating a new object."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val SetTerm = TermName("Set")
          private val ApplyTerm = TermName("apply")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case a @ Apply(TypeApply(Select(Select(_, SetTerm), ApplyTerm), _), List())
                  if a.tpe.toString.startsWith("scala.collection.immutable.") =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
