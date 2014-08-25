package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 *
 *         Inspired by http://codenarc.sourceforge.net/codenarc-rules-basic.html#BrokenOddnessCheck
 */
class BrokenOddness extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Apply(Select(_, TermName("$percent")), List(Literal(Constant(2)))),
            TermName("$eq$eq")), List(Literal(Constant(1)))) =>
            context.warn("Broken odd check", tree.pos, Levels.Warning,
              "Potentially broken odd check. " + tree.toString().take(500) + "." +
                "Code that attempts to check for oddness using x % 2 == 1 will fail on negative numbers. Consider using x % 2 != 0",
              BrokenOddness.this)
          case _ => continue(tree)
        }
      }
    }
  }
}