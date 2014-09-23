package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{Levels, Inspection, InspectionContext, Inspector}

/** @author Stephen Samuel */
class PredefSeqUse extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Select(Select(_, TermName("Predef")), TermName("Set")) =>
            context.warn("Predef.Set is mutable",
              tree.pos,
              Levels.Info,
              "Predef.Set aliases scala.collection.mutable.Set. Did you intend to use an immutable collection.",
              PredefSeqUse.this)
          case _ => continue(tree)
        }
      }
    }
  }
}