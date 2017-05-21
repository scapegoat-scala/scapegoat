package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Josh Rosen */
class CollectionIndexOnNonIndexedSeq extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def isSeq(t: Tree) = t.tpe <:< typeOf[Seq[_]]
      private def isIndexedSeq(t: Tree) = t.tpe <:< typeOf[IndexedSeq[_]]
      private def isLiteral(t: Tree) = t match {
        case Literal(_) => true
        case _ => false
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, TermName("apply")), List(idx)) if isSeq(lhs) && !isIndexedSeq(lhs) && !isLiteral(idx)=>
            context.warn("Seq.apply() on a non-IndexedSeq may cause performance problems",
              tree.pos,
              Levels.Warning,
              tree.toString().take(100),
              CollectionIndexOnNonIndexedSeq.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
