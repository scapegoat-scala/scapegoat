package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class PredefIterableIsMutable
    extends Inspection(
      text = "Default Iterable is mutable",
      defaultLevel = Levels.Info,
      description = "Checks for use of mutable Iterable.",
      explanation =
        "Iterable aliases scala.collection.mutable.Iterable. Did you intend to use an immutable Iterable?"
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          override def inspect(tree: Tree): Unit = {
            tree match {
              case DefDef(_, _, _, _, _, _) if tree.symbol.isAccessor =>
              case TypeTree() if tree.tpe.erasure.toString() == "Iterable[Any]" =>
                context.warn(tree.pos, self)
              case _ => continue(tree)
            }
          }
        }
    }
}
