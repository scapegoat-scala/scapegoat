package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ComparisonWithSelf extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      def containsAssignment(tree: Tree) = tree match {
        case Assign(_, _) => true
        case _            => false
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(left, TermName("$eq$eq")), List(right)) =>
            if (left.toString() == right.toString())
              context.warn("Comparision with self",
                tree.pos,
                Levels.Warning,
                "Comparision with self will always yield true",
                ComparisonWithSelf.this)
          case _ => continue(tree)
        }
      }
    }
  }
}