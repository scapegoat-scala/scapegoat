package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class ComparisonWithSelf extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    def containsAssignment(tree: Tree) = tree match {
      case Assign(_, _) => true
      case _ => false
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(left, TermName("$eq$eq")), List(right)) =>
          if (left.toString() == right.toString())
            feedback
              .warn("Comparision with self", tree.pos, Levels.Error, "Comparision with self will always yield true")
        case _ => super.traverse(tree)
      }
    }
  }
}
