package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel
  *
  *         Inspired by http://codenarc.sourceforge.net/codenarc-rules-basic.html#BrokenOddnessCheck
  * */
class BrokenOddness extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(Apply(Select(_, TermName("$percent")), List(Literal(Constant(2)))),
        TermName("$eq$eq")), List(Literal(Constant(1)))) =>
          feedback.warn("Broken odd check", tree.pos, Levels.Warning,
            "Potentially broken odd check. " + tree.toString().take(500) + "." +
              "Code that attempts to check for oddness using x % 2 == 1 will fail on negative numbers. Consider using x % 2 != 0")
        case _ => super.traverse(tree)
      }
    }
  }

}
