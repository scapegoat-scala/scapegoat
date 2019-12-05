package com.sksamuel.scapegoat.inspections.controlflow

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

class RepeatedIfElseBody extends Inspection("Repeated body of if main and else branch", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def isRepeated(mainBranch: Tree, elseBranch: Tree): Boolean = {
        mainBranch.toString() == elseBranch.toString()
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case If(_, mainBranch, elseBranch) if isRepeated(mainBranch, elseBranch) =>
            context.warn(tree.pos, self,
              "Main and else branches of if are repeated: " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }
    }
  }
}