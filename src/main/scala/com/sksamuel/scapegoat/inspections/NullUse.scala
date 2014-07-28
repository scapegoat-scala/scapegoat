package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Inspection, Levels, Reporter}

/** @author Stephen Samuel */
class NullUse extends Inspection {

  import scala.reflect.runtime.universe
  import scala.reflect.runtime.universe._

  override def traverser(reporter: Reporter) = new universe.Traverser {

    def containsNull(trees: List[universe.Tree]) = trees.contains(Literal(Constant(null)))

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Apply(_, args) if containsNull(args) =>
          reporter.warn("null use", tree, Levels.Error, "null used near: " + tree.toString().take(300))
        case Literal(Constant(null)) =>
          reporter.warn("null use", tree, Levels.Error, "null used: " + tree.toString().take(300))
        case _ => super.traverse(tree)
      }
    }
  }
}
