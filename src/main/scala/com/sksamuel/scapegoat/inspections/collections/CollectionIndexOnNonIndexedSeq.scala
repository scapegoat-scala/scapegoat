package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Josh Rosen
 */
class CollectionIndexOnNonIndexedSeq
    extends Inspection(
      text = "Use of apply method on a non-indexed Seq",
      defaultLevel = Levels.Warning,
      description = "Checks for indexing on a Seq which is not an IndexedSeq.",
      explanation = "Using an index to access elements of an IndexedSeq may cause performance problems."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def isLiteral(t: Tree) =
            t match {
              case Literal(_) => true
              case _          => false
            }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Apply(Select(lhs, TermName("apply")), List(idx))
                  if isSeq(lhs) && !isIndexedSeq(lhs) && !isLiteral(idx) =>
                context.warn(tree.pos, self, tree.toString.take(100))
              case _ => continue(tree)
            }
          }
        }
    }
}
