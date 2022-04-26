package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class CollectionNamingConfusion
    extends Inspection(
      text = "Collection naming confusion",
      defaultLevel = Levels.Info,
      description = "Checks for variables that are confusingly named.",
      explanation =
        "E.g. an instance of a Set is confusingly referred to by a variable called/containing list, or the other way around."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def isNamedSet(name: String): Boolean =
            name.trim == "set" || name.trim.endsWith("Set") || name.matches(".*Set[A-Z].*")
          private def isNamedList(name: String): Boolean =
            name.trim == "list" || name.trim.endsWith("List") || name.matches(".*List[A-Z].*")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case ValDef(_, TermName(name), tpt, _) if isSet(tpt) && isNamedList(name) =>
                context.warn(
                  tree.pos,
                  self,
                  tree.toString.take(300),
                  "An instance of a Set is confusingly referred to by a variable called/containing list."
                )
              case ValDef(_, TermName(name), tpt, _) if isList(tpt) && isNamedSet(name) =>
                context.warn(
                  tree.pos,
                  self,
                  tree.toString.take(300),
                  "An instance of a List is confusingly referred to by a variable called/containing set."
                )
              case _ => continue(tree)
            }
          }
        }
    }
}
