package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class EmptyElseBlock extends Inspection {

  import universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case universe.If(cond, thenp, elsep) =>
          if (elsep.children.isEmpty)
            reporter.warn("Empty else block", tree, Levels.Warning, "Empty else block " + cond.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
