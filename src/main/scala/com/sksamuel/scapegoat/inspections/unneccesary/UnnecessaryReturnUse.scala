package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class UnnecessaryReturnUse extends Inspection("Unnecessary return", Levels.Info,
  "Scala returns the value of the last expression in a block. Use of return here is not idiomatic scala") {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case DefDef(_, _, _, _, _, Block(_, Return(_))) =>
            context.warn(tree.pos, self)
          case _ => continue(tree)
        }
      }
    }
  }
}