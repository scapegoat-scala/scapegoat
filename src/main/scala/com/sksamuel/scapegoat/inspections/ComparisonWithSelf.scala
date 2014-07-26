package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.api
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/** @author Stephen Samuel */
class ComparisonWithSelf extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {

    def containsAssignment(tree: api.JavaUniverse#Tree) = tree match {
      case universe.Assign(_, _) => true
      case _ => false
    }

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Apply(Select(left, TermName("$eq$eq")), List(right)) =>
          if (left.toString() == right.toString())
            reporter.warn("Comparision with self", tree.pos.line, level = Levels.Major)
        case _ => super.traverse(tree)
      }
    }
  }
}
