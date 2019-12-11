package com.sksamuel.scapegoat.inspections.style

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class SimplifyBooleanExpression extends Inspection("Simplify boolean expressions", Levels.Info) {
  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Equals = TermName("$eq$eq")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(_, Equals), List(Literal(Constant(false)))) =>
            context.warn(tree.pos, self,
              "Boolean expressions such as x == false can be re-written as !x: " + tree.toString().take(200))
          case _ => continue(tree)
        }
      }
    }
  }
}
