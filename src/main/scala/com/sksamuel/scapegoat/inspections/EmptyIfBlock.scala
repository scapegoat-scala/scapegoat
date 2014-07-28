package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class EmptyIfBlock extends Inspection {

  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case If(_, Literal(Constant(())), _) =>
          reporter.warn("Empty if statement", tree, level = Levels.Warning, tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }
  }
}
