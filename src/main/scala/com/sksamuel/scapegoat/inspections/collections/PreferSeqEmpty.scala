package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
class PreferSeqEmpty
    extends Inspection(
      text = "Prefer Seq.empty",
      defaultLevel = Levels.Info,
      description = "Checks for use of Seq().",
      explanation =
        "`Seq[T]()` allocates an intermediate object. Consider `Seq.empty` which returns a singleton instance without creating a new object."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private val ApplyTerm = TermName("apply")
          private val SeqTerm = TermName("Seq")

          override def inspect(tree: Tree): Unit = {
            tree match {
              case a @ Apply(TypeApply(Select(Select(_, SeqTerm), ApplyTerm), _), List())
                  if !a.tpe.toString.startsWith("scala.collection.mutable.") =>
                context.warn(tree.pos, self, tree.toString.take(500))
              case _ => continue(tree)
            }
          }
        }
    }
}
