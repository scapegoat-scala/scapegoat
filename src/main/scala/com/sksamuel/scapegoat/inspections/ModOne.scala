package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel
  *
  *         Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#INT_BAD_REM_BY_1
  **/
class ModOne extends Inspection {

  override def traverser(reporter: Reporter) = new universe.Traverser with SuppressAwareTraverser {

    import scala.reflect.runtime.universe._

    override def traverse(tree: scala.reflect.runtime.universe.Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(_, TermName("$percent")), List(Literal(Constant(1)))),
        TermName("$eq$eq")), List(Literal(Constant(1)))) =>
          reporter.warn("Integer mod one", tree, level = Levels.Warning,
            "Any expression x % 1 will always return 0. " + tree.toString().take(100))
        case _ => super.traverse(tree)
      }
    }
  }

}
