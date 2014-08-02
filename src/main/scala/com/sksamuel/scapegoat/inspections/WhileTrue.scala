package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat._

import scala.tools.nsc.Global

/** @author Stephen Samuel
  * */
class WhileTrue extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    override def traverse(tree: Tree): Unit = {
      tree match {
        case LabelDef(name, _, If(cond, _, _))
          if isWhile(name) && isConstantCondition(cond) =>
          feedback.warn("While true loop", tree.pos, Levels.Warning,
            "A while true loop is unlikely to be meant for production:" + tree.toString().take(500))
        case LabelDef(name, _, Block(_, If(cond, _, _)))
          if isWhile(name) && isConstantCondition(cond) =>
          feedback.warn("While true loop", tree.pos, Levels.Warning,
            "A do while true loop is unlikely to be meant for production:" + tree.toString().take(500))
        case _ => super.traverse(tree)
      }
    }

    private def isConstantCondition(tree: Tree): Boolean = tree match {
      case Literal(Constant(true)) => true
      case _ => false
    }

    private def isWhile(name: TermName): Boolean = {
      name.toString.startsWith("while$") || name.toString.startsWith("doWhile$")
    }
  }
}