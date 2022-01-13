package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class PredefTraversableIsMutable
    extends Inspection(
      text = "Traversable is mutable",
      defaultLevel = Levels.Info,
      description = "",
      explanation =
        "Traversable aliases scala.collection.mutable.Traversable. Did you intend to use an immutable Traversable?"
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(_, _, _, _, _, _) if tree.symbol.isAccessor              =>
              case TypeTree() if tree.tpe.erasure.toString() == "Traversable[Any]" => warn(tree)
              case _                                                               => continue(tree)
            }
          }

          def warn(tree: Tree): Unit =
            context.warn(tree.pos, self)
        }
    }
}
