package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ExpressionAsStatement extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private def checkStatements(statements: Seq[Tree]): Unit = {
        statements foreach {
          // ignore super calls
          case Apply(Select(_, nme.CONSTRUCTOR), _) =>
          case Assign(_, _) =>
          // seems to be some odd cases where empty trees with no source appear
          // https://github.com/sksamuel/sbt-scapegoat/issues/3
          case EmptyTree =>
          case stmt if stmt.isDef =>
          case stmt if stmt.tpe != null && !(stmt.tpe <:< typeOf[Unit]) =>
            context.warn("Expression as statement", stmt.pos, Levels.Warning,
              "Expression as statement at " + stmt.toString().take(500), ExpressionAsStatement.this)
          case _ =>
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Block(statements, _) => checkStatements(statements)
          case ClassDef(_, _, _, Template(_, _, statements)) => checkStatements(statements)
          case ModuleDef(_, _, Template(_, _, statements)) => checkStatements(statements)
          case _ =>
        }
        continue(tree)
      }
    }
  }
}
