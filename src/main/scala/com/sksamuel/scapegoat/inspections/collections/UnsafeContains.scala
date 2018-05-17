package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnsafeContains extends Inspection("Unsafe contains", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {
      import context.global._
      import treeInfo.Applied

      private val Contains = TermName("contains")
      private val Seq = typeOf[Seq[_]].typeSymbol
      private def isSeq(tree: Tree): Boolean = tree.tpe.widen.baseClasses contains Seq
      private def isCompatibleType(container: Tree, value: Tree) = container.tpe baseType Seq match {
        case TypeRef(a, Seq, elem :: Nil) if elem.isInstanceOf[Any] && elem <:< value.tpe => true
        case TypeRef(a, Seq, elem :: Nil) => value.tpe <:< elem
        case _                            => false
      }
      override def inspect(tree: Tree): Unit = tree match {
        case Applied(Select(lhs, Contains), _, (arg :: Nil) :: Nil) if isSeq(lhs) && !isCompatibleType(lhs, arg) =>
          context.warn(tree.pos, self, tree.toString().take(300))
        case _ =>
          continue(tree)
      }
    }
  }
}
