package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnsafeContains extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply  new context.Traverser {

      import context.global._

      private val Contains = TermName("contains")
      private val Seq = typeOf[Seq[_]]
      private def isSeq(tree: Tree): Boolean = tree.tpe <:< Seq
      private def isCompatibleType(container: Tree, value: Tree): Boolean = {
        val v = value.tpe.underlying.typeSymbol.tpe
        container.tpe.typeArgs.headOption match {
          case None => false
          case Some(c) => v <:< c
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(lhs, Contains), List(tpe)), List(arg)) =>
            if (isSeq(lhs) && !isCompatibleType(lhs, arg)) {
              context.warn("Unsafe contains", tree.pos, Levels.Error, tree.toString().take(300), UnsafeContains.this)
            }
          case _ => continue(tree)
        }
      }
    }
  }
}