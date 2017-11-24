package com.sksamuel.scapegoat.inspections.controlflow

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 */
class WhileTrue extends Inspection("While true loop", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case LabelDef(name, _, If(cond, _, _)) if isWhile(name) && isConstantCondition(cond) =>
            context.warn(tree.pos, self,
              "A while true loop is unlikely to be meant for production: " + tree.toString().take(500))
          case LabelDef(name, _, Block(_, If(cond, _, _))) if isWhile(name) && isConstantCondition(cond) =>
            context.warn(tree.pos, self,
              "A do while true loop is unlikely to be meant for production: " + tree.toString().take(500))
          case _ => continue(tree)
        }
      }

      private def isConstantCondition(tree: Tree): Boolean = tree match {
        case Literal(Constant(true)) => true
        case _                       => false
      }

      private def isWhile(name: TermName): Boolean = {
        name.toString.startsWith("while$") || name.toString.startsWith("doWhile$")
      }
    }
  }
}