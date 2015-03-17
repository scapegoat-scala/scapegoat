package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Levels, Inspection, InspectionContext, Inspector }

/** @author Stephen Samuel */
class PredefSeqIsMutable extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(mods, _, _, _, _, _) if tree.symbol.isAccessor =>
          case TypeTree() if tree.tpe.erasure.toString() == "Seq[Any]" => warn(tree)
          case _ => continue(tree)
        }
      }

      def warn(tree: Tree): Unit = {
        context.warn("Predef.Seq is mutable",
          tree.pos,
          Levels.Info,
          "Predef.Seq aliases scala.collection.mutable.Seq. Did you intend to use an immutable Seq?",
          PredefSeqIsMutable.this)
      }
    }
  }
}