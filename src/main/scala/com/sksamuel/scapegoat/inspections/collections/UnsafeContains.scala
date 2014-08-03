package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnsafeContains extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def traverse(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(lhs, TermName("contains")), List(tpe)), List(arg)) =>
            if (lhs.tpe <:< typeOf[Seq[_]] && !(arg.tpe <:< lhs.tpe.typeArgs.head)) {
              context.warn("Unsafe contains", tree.pos, Levels.Error, tree.toString().take(300))
            }
          case _ => super.traverse(tree)
        }
      }
    }
  }
}