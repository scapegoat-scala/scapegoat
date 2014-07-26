package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Level, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class EmptyElseBlock extends Inspection {

  override def traverser(reporter: Reporter) = new universe.Traverser {
    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case universe.If(cond, thenp, elsep) =>
          if (elsep.children.isEmpty)
            reporter
              .warn("Empty else block",
                tree.pos.line,
                level = Levels.Minor,
                "Empty else block " + cond.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }
}
