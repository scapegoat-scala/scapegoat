package com.sksamuel.scapegoat.inspections.equality

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ComparisonWithSelf extends Inspection("Comparision with self", Levels.Warning,
  "Comparision with self will always yield true") {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      def containsAssignment(tree: Tree) = tree match {
        case Assign(_, _) => true
        case _            => false
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(left, TermName("$eq$eq" | "$bang$eq")), List(right)) =>
            if (left.toString() == right.toString())
              context.warn(tree.pos,self)
          case _ => continue(tree)
        }
      }
    }
  }
}