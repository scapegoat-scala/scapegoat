package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnnecessaryIf extends Inspection("Unnecessary if condition.", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case If(_, Literal(Constant(true)), Literal(Constant(false))) =>
            context.warn(tree.pos, self,
              "If comparison is not needed. Use the condition. Eg, instead of if (a == b) true else false, simply use a == b. : " + tree
                .toString().take(500))
          case If(_, Literal(Constant(false)), Literal(Constant(true))) =>
            context.warn(tree.pos, self,
              "If comparison is not needed. Use the negated condition. Eg, instead of if (a == b) false else true, simply use !(a == b). : " + tree
                .toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}