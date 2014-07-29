package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel
  *
  *         Inspired by http://codenarc.sourceforge.net/codenarc-rules-basic.html#BrokenOddnessCheck
  * */
class BrokenOddness extends Inspection {

  override def traverser(reporter: Reporter) = new universe.Traverser with SuppressAwareTraverser {

    import scala.reflect.runtime.universe._

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(_, TermName("$percent")), List(Literal(Constant(2)))),
        TermName("$eq$eq")), List(Literal(Constant(1)))) =>
          reporter.warn("Broken odd check", tree, level = Levels.Warning,
            "Potentially broken odd check. " + tree.toString().take(500) + "." +
              "Code that attempts to check for oddness using x % 2 == 1 will fail on negative numbers. Consider using x % 2 != 0")
        case _ => super.traverse(tree)
      }
    }
  }

}
