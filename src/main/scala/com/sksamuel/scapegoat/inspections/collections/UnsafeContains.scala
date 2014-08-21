package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnsafeContains extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {
      import context.global._
      import treeInfo.Applied

      private val Contains = TermName("contains")
      private val Seq = typeOf[Seq[_]].typeSymbol
      private def isSeq(tree: Tree): Boolean = tree.tpe.widen.baseClasses contains Seq
      private def isCompatibleType(container: Tree, value: Tree) = container.tpe baseType Seq match {
        case TypeRef(_, Seq, elem :: Nil) => value.tpe <:< elem
        case _                            => false
      }
      override def inspect(tree: Tree): Unit = tree match {
        case Applied(Select(lhs, Contains), _, (arg :: Nil) :: Nil) if isSeq(lhs) && !isCompatibleType(lhs, arg) =>
          context.warn("Unsafe contains", tree.pos, Levels.Error, tree.toString().take(300), UnsafeContains.this)
        case _ =>
          continue(tree)
      }
    }
  }
}
