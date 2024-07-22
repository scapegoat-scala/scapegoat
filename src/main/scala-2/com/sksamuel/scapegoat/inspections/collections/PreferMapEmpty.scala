package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

class PreferMapEmpty
    extends Inspection(
      text = "Prefer Map.empty",
      defaultLevel = Levels.Info,
      description = "Checks for use of Map().",
      explanation =
        "`Map[K,V]()` allocates an intermediate object. Consider `Map.empty` which returns a singleton instance without creating a new object."
    ) {

  def inspector(ctx: InspectionContext): Inspector =
    new Inspector(ctx) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val MapTerm = TermName("Map")
          private val ApplyTerm = TermName("apply")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case a @ Apply(TypeApply(Select(Select(_, MapTerm), ApplyTerm), _), List())
                  if a.tpe.toString.startsWith("scala.collection.immutable.") =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
