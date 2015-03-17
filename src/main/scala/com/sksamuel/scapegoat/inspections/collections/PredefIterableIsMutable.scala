package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class PredefIterableIsMutable extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(mods, _, _, _, _, _) if tree.symbol.isAccessor =>
          case TypeTree() if tree.tpe.erasure.toString() == "Iterable[Any]" => warn(tree)
          case _ => continue(tree)
        }
      }

      def warn(tree: Tree): Unit = {
        context.warn("Default Iterable is mutable",
          tree.pos,
          Levels.Info,
          "Iterable aliases scala.collection.mutable.Iterable. Did you intend to use an immutable Iterable?",
          PredefIterableIsMutable.this)
      }
    }
  }
}