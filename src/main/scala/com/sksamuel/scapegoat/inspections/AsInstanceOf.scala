package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Reporter}

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/** @author Stephen Samuel */
object AsInstanceOf extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: universe.Tree): Unit = {
      tree match {
        case Select(_, TermName("asInstanceOf")) => reporter.warn("Use of asInstanceOf", tree.pos.line)
        case _ => super.traverse(tree)
      }
    }
  }
}
