package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class JavaConversionsUse extends Inspection {
  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case universe.Select(_, universe.TermName("JavaConversions")) =>
          reporter.warn("Java conversions", tree.pos.line, "Use of java conversions " + tree.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
