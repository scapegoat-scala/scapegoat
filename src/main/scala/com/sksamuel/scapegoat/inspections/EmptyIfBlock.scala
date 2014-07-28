package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Level, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class EmptyIfBlock extends Inspection {

  import universe._

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
