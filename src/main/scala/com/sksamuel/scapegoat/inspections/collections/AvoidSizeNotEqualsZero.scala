package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class AvoidSizeNotEqualsZero extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Size = TermName("size")
      private val Length = TermName("length")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Select(_, Length | Size), TermName("$bang$eq")), List(Literal(Constant(0)))) =>
            context.warn("Avoid Traversable.size == 0", tree.pos, Levels.Warning,
              "Traversable.size is slow for some implementations. Prefer .nonEmpty which is O(1): " + tree
                .toString().take(100), AvoidSizeNotEqualsZero.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
