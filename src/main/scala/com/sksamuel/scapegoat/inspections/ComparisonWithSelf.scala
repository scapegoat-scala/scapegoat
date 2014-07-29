package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class ComparisonWithSelf extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser with SuppressAwareTraverser {

    def containsAssignment(tree: Tree) = tree match {
      case universe.Assign(_, _) => true
      case _ => false
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(left, TermName("$eq$eq")), List(right)) =>
          if (left.toString() == right.toString())
            reporter.warn("Comparision with self", tree, level = Levels.Error)
        case _ => super.traverse(tree)
      }
    }
  }
}
