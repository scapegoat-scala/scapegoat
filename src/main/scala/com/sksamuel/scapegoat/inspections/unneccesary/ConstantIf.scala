package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ConstantIf extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case If(cond, thenp, elsep) =>
            if (cond.toString() == "false" || cond.toString() == "true")
              context.warn("Constant if expression", tree.pos, Levels.Warning,
                "Constant if expression " + tree.toString().take(500))
          case _ => super.traverse(tree)
        }
      }
    }
  }
}
