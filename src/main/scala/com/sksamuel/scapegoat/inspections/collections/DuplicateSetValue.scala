package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class DuplicateSetValue extends Inspection("Duplicated set value", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def hasDuplicates(trees: List[Tree]): Boolean = {
        val values: Set[Any] = trees.map {
          case Literal(Constant(x)) => x
          case x                    => x
        }.toSet
        values.size < trees.size
      }

      private def warn(tree: Tree) = {
        context.warn(tree.pos, self,
          "A set value is overwritten by a later entry: " + tree.toString().take(100))
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(TypeApply(Select(Select(_, TermName("Set")), TermName("apply")), _), args) if hasDuplicates(args) => warn(tree)
          case _ => continue(tree)
        }
      }
    }
  }
}
