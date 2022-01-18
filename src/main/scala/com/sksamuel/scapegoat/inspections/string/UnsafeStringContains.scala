package com.sksamuel.scapegoat.inspections.string

import com.sksamuel.scapegoat._

/**
 * @author
 *   Zack Grannan
 */
class UnsafeStringContains
    extends Inspection(
      text = "Unsafe string contains",
      defaultLevel = Levels.Error,
      description = "Checks for String.contains(value) for invalid types.",
      explanation =
        "String.contains() accepts arguments af any type, which means you might be checking if your string contains an element of an unrelated type."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._
          import treeInfo.Applied

          private val Contains = TermName("contains")

          private def isChar(tree: Tree) = tree.tpe.widen.baseClasses.contains(typeOf[Char].typeSymbol)

          private def isString(tree: Tree): Boolean = {
            tree.tpe.widen.baseClasses.contains(typeOf[CharSequence].typeSymbol) || (tree match {
              case Apply(left, _) =>
                Set("scala.LowPriorityImplicits.wrapString", "scala.Predef.augmentString")(
                  left.symbol.fullName
                )
              case _ => false
            })
          }

          private def isCompatibleType(value: Tree) = isString(value) || isChar(value)

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Applied(Select(lhs, Contains), targ :: Nil, (_ :: Nil) :: Nil)
                  if isString(lhs) && !isCompatibleType(targ) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case Applied(Select(lhs, Contains), _, (arg :: Nil) :: Nil)
                  if isString(lhs) && !isCompatibleType(arg) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ =>
                continue(tree)
            }
          }
        }
    }
}
