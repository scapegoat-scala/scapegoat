package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class BrokenOddness extends Inspection {

  override def traverser(reporter: Reporter) = new universe.Traverser {

    import scala.reflect.runtime.universe._

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(_, TermName("$percent")), List(Literal(Constant(2)))),
        TermName("$eq$eq")), List(Literal(Constant(1)))) =>
          reporter.warn("Broken odd check", tree, level = Levels.Warning,
            "Broken odd check near " + tree.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }

}
