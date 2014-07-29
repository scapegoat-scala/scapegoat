package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class JavaConversionsUse extends Inspection {

  import universe._

  override def traverser(reporter: Reporter) = new Traverser with SuppressAwareTraverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case Select(_, TermName("JavaConversions")) =>
          reporter.warn("Java conversions", tree, Levels.Error, "Use of java conversions " + tree.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
