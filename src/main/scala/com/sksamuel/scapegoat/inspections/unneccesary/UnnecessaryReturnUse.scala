package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnnecessaryReturnUse extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def traverse(tree: Tree): Unit = {
        tree match {
          case Block(_, Return(expr)) =>
            context.warn("Unnecessary return", tree.pos, Levels.Error,
              "Scala returns the value of the last expression in a function. Use of return is not needed here")
          case _ => super.traverse(tree)
        }
      }
    }
  }
}