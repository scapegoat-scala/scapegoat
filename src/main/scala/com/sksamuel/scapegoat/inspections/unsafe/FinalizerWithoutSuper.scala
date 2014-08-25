package com.sksamuel.scapegoat.inspections.unsafe

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class FinalizerWithoutSuper extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Finalize = TermName("finalize")
      private def containsSuper(tree: Tree): Boolean = tree match {
        case Apply(Select(Super(_, _), Finalize), List()) => true
        case Block(stmts, expr) => (stmts :+ expr).exists(containsSuper)
        case _ => false
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case dd @ DefDef(mods, Finalize, _, _, tpt, rhs) if tpt.tpe <:< typeOf[Unit] =>
            if (!containsSuper(rhs))
              context.warn("Finalizer without super",
                tree.pos,
                Levels.Warning,
                "Finalizers should call super.finalize() to ensure superclasses are able to run any finalization logic",
                FinalizerWithoutSuper.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
