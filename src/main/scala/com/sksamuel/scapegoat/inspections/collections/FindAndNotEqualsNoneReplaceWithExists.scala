package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

class FindAndNotEqualsNoneReplaceWithExists
    extends Inspection(
      text = "find(x => ) != None instead of exists(x =>)",
      defaultLevel = Levels.Info,
      description = "Checks whether `find()` can be replaced with exists().",
      explanation = "`find() != None` can be replaced with `exists()`, which is more concise."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(
                    Select(Apply(Select(_, TermName("find")), _), TermName("$bang$eq")),
                    List(Select(_, TermName("None")))
                  ) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
