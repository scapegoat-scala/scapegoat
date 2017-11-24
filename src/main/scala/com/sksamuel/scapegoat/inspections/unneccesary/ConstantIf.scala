package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 *
 *         Checks for if statements where the condition evaluates to a constant true or a constant false.
 *
 */
class ConstantIf extends Inspection("Constant if expression", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          // ignore while loops, this will be picked up by the WhileTrue inspection
          case LabelDef(_, _, _) =>
          case If(cond, thenp, elsep) =>
            if (cond.toString() == "false" || cond.toString() == "true")
              context.warn(tree.pos, self,
                "Constant if expression " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}
