package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class UnsafeContains
    extends Inspection(
      text = "Unsafe contains",
      defaultLevel = Levels.Error,
      description = "Checks `Seq.contains()` and `Option.contains()` for unrelated types.",
      explanation =
        "`contains()` accepts arguments af any type, which means you might be checking if your collection contains an element of an unrelated type."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {
          import context.global._
          import treeInfo.Applied

          private val Contains = TermName("contains")
          private val Seq = typeOf[Seq[Any]].typeSymbol
          private val Option = typeOf[Option[Any]].typeSymbol
          private def isSeqOrOption(tree: Tree): Boolean = {
            val baseClasses = tree.tpe.widen.baseClasses
            baseClasses.contains(Seq) || baseClasses.contains(Option)
          }

          private def isCompatibleType(container: Tree, value: Tree, typ: Symbol): Boolean =
            container.tpe baseType typ match {
              case TypeRef(_, _, elem :: Nil) if elem.isInstanceOf[Any] && elem <:< value.tpe => true
              case TypeRef(_, _, elem :: Nil) => value.tpe <:< elem
              case _                          => false
            }

          private def isCompatibleType(container: Tree, value: Tree): Boolean =
            isCompatibleType(container, value, Seq) || isCompatibleType(container, value, Option)

          override def inspect(tree: Tree): Unit =
            tree match {
              case Applied(Select(lhs, Contains), _, (arg :: Nil) :: Nil)
                  if isSeqOrOption(lhs) && !isCompatibleType(lhs, arg) =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ =>
                continue(tree)
            }
        }
    }
}
