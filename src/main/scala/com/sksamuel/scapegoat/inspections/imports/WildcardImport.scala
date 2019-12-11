package com.sksamuel.scapegoat.inspections.imports

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class WildcardImport extends Inspection("Wildcard import", Levels.Warning) {

  override def inspector(context: InspectionContext): Inspector = new Inspector(context) {

    import context.global._

    private def isWildcard(trees: List[ImportSelector]): Boolean = trees.exists(_.name == nme.WILDCARD)

    override def postTyperTraverser = Some apply new context.Traverser {

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Import(expr, selector) if isWildcard(selector) =>
            context.warn(tree.pos, self, "Wildcard import used: " + tree.toString())
          case _ => continue(tree)
        }
      }
    }
  }
}
