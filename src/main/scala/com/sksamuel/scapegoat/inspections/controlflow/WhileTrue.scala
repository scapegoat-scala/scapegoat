package com.sksamuel.scapegoat.inspections.controlflow

import com.sksamuel.scapegoat._

/**
 * @author Stephen Samuel
 */
class WhileTrue extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case LabelDef(name, _, If(cond, _, _)) if isWhile(name) && isConstantCondition(cond) =>
            context.warn("While true loop", tree.pos, Levels.Warning,
              "A while true loop is unlikely to be meant for production: " + tree.toString().take(500), WhileTrue.this)
          case LabelDef(name, _, Block(_, If(cond, _, _))) if isWhile(name) && isConstantCondition(cond) =>
            context.warn("While true loop", tree.pos, Levels.Warning,
              "A do while true loop is unlikely to be meant for production: " + tree.toString().take(500), WhileTrue.this)
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