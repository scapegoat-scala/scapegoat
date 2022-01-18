package com.sksamuel.scapegoat.inspections.imports

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class WildcardImport
    extends Inspection(
      text = "Wildcard imports",
      defaultLevel = Levels.Warning,
      description = "Checks for wildcard imports.",
      explanation =
        "Avoid using wildcard imports, unless you are importing more than a few entities. Wildcard imports make the code more difficult to maintain."
    ) {

  override def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {

      import context.global._

      private def isWildcard(trees: List[ImportSelector]): Boolean = trees.exists(_.name == nme.WILDCARD)

      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Import(_, selector) if isWildcard(selector) =>
                context.warn(tree.pos, self, tree.toString)
              case _ => continue(tree)
            }
          }
        }
    }
}
