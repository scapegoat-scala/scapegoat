package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel
  *
  *         Inspired by http://findbugs.sourceforge.net/bugDescriptions.html#INT_BAD_REM_BY_1
  **/
class ModOne extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(_, TermName("$percent")), List(Literal(Constant(1)))),
        TermName("$eq$eq")), List(Literal(Constant(1)))) =>
          feedback.warn("Integer mod one", tree.pos, Levels.Warning,
            "Any expression x % 1 will always return 0. " + tree.toString().take(300))
        case _ => super.traverse(tree)
      }
    }
  }

}
