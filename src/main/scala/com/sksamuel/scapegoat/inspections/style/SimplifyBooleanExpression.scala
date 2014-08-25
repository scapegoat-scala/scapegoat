package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Stephen Samuel */
class SimplifyBooleanExpression extends Inspection {
  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Equals = TermName("$eq$eq")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(lhs, Equals), List(Literal(Constant(false)))) =>
            context.warn("Simplify boolean expressions",
              tree.pos,
              Levels.Info,
              "Boolean expressions such as x == false can be re-written as !x: " + tree.toString().take(200),
              SimplifyBooleanExpression.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
