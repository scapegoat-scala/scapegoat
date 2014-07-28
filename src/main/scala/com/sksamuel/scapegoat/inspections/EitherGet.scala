package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/** @author Stephen Samuel */
class EitherGet extends Inspection {
  val optionSymbol = rootMirror.staticClass("scala.Option")
  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Select(Select(_, TermName("right")), TermName("get")) =>
          reporter.warn("Use of Either Right Projection get", tree, Levels.Error)
        case Select(Select(_, TermName("left")), TermName("get")) =>
          reporter.warn("Use of Either Left Projection get", tree, Levels.Error)
        case _ => super.traverse(tree)
      }
    }
  }
}
