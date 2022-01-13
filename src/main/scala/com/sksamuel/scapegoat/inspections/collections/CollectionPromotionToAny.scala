package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel This inspection was inspired by http://p5wscala.wordpress.com/scalaprocessing-gotchas/#t2
 */
class CollectionPromotionToAny
    extends Inspection(
      text = "Collection promotion to Any",
      defaultLevel = Levels.Warning,
      description = "Checks for collection operations that promote the collection to Any.",
      explanation =
        "The `:+` (append) operator on collections accepts any argument you give it, which means that you can end up with e.g. `Seq[Any]` if your types don't match."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def isSeq(symbol: Symbol): Boolean = {
            val full = symbol.typeSignature.resultType.typeSymbol.fullName
            val immutableCollection = full.startsWith("scala.collection.immutable") &&
              (full.endsWith("List") || full.endsWith("Set") || full.endsWith("Seq") || full.endsWith(
                "Vector"
              ))

            immutableCollection || full == "scala.collection.Seq"
          }

          private def isAny(tree: Tree): Boolean = tree.toString() == "Any"
          private def isAny(symbol: Symbol): Boolean =
            symbol.typeSignature.resultType.typeArgs.headOption match {
              case Some(t) => t.toString == "Any"
              case None    => false
            }

          private def isAnySeq(tree: Tree): Boolean =
            tree match {
              case select @ Select(_, _) if select.symbol != null =>
                isSeq(select.symbol) && isAny(select.symbol)
              case _ => false
            }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case TypeApply(Select(l, TermName("$colon$plus")), a :: _) =>
                if (!isAnySeq(l) && isAny(a))
                  context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
