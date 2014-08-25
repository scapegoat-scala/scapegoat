package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnnecessaryReturnUse extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Block(_, Return(expr)) =>
            context.warn("Unnecessary return", tree.pos, Levels.Info,
              "Scala returns the value of the last expression in a block. Use of return here is not idomatic scala",
              UnnecessaryReturnUse.this)
          case _ => continue(tree)
        }
      }
    }
  }
}