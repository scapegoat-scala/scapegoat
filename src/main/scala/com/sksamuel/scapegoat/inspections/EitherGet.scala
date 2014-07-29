package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/** @author Stephen Samuel */
class EitherGet extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser with SuppressAwareTraverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Select(Select(_, TermName("right")), TermName("get")) =>
          reporter.warn("Use of Either Right Projection get", tree, Levels.Error, tree.toString().take(500))
        case Select(Select(_, TermName("left")), TermName("get")) =>
          reporter.warn("Use of Either Left Projection get", tree, Levels.Error, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
