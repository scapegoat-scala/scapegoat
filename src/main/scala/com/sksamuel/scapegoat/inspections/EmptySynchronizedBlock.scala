package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

/** @author Stephen Samuel */
class EmptySynchronizedBlock extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case If(cond, thenp, elsep) =>
          if (thenp.children.isEmpty)
            reporter.warn("Empty if statement", tree, level = Levels.Warning,
              "Empty if statement " + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
