package com.sksamuel.scapegoat.inspections.option

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/** @author Stephen Samuel */
class ImpossibleOptionSizeCondition extends Inspection("Impossible Option.size condition", Levels.Error) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val Opt2Iterable = TermName("option2Iterable")
      private val Size = TermName("size")
      private val Greater = TermName("$greater")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Apply(Select(Select(Apply(TypeApply(Select(_, Opt2Iterable), _), _), Size), Greater),
            List(Literal(Constant(x: Int)))) if x > 1 =>
            context.warn(tree.pos, self,
              "Option.size > " + x + " can never be true: " + tree.toString().take(200))
          case _ => continue(tree)
        }
      }
    }
  }

}
