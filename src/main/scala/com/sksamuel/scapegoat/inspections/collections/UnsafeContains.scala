package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnsafeContains extends Inspection("Unsafe contains", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {
      import context.global._
      import treeInfo.Applied

      private val Contains = TermName("contains")
      private val Seq = typeOf[Seq[Any]].typeSymbol
      private val Option = typeOf[Option[Any]].typeSymbol
      private def isSeqOrOption(tree: Tree): Boolean = {
        val baseClasses = tree.tpe.widen.baseClasses
        baseClasses.contains(Seq) || baseClasses.contains(Option)
      }

      private def isCompatibleType(container: Tree, value: Tree, typ: Symbol): Boolean = container.tpe baseType typ match {
        case TypeRef(a, typ, elem :: Nil) if elem.isInstanceOf[Any] && elem <:< value.tpe => true
        case TypeRef(a, typ, elem :: Nil) => value.tpe <:< elem
        case _                            => false
      }

      private def isCompatibleType(container: Tree, value: Tree): Boolean = {
        isCompatibleType(container, value, Seq) || isCompatibleType(container, value, Option)
      }

      override def inspect(tree: Tree): Unit = tree match {
        case Applied(Select(lhs, Contains), _, (arg :: Nil) :: Nil) if isSeqOrOption(lhs) && !isCompatibleType(lhs, arg) =>
          context.warn(tree.pos, self, tree.toString().take(300))
        case _ =>
          continue(tree)
      }
    }
  }
}
