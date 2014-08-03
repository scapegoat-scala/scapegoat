package com.sksamuel.scapegoat.inspections.unneccesary

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class ExpressionAsStatement extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      private def checkStatements(statements: Seq[Tree]): Unit = {
        statements foreach {
          case Apply(Select(_, name), _) if name.toString == "<init>" =>
          case Assign(_, _) =>
          case stmt if stmt.isDef =>
          case stmt if stmt.tpe != null && stmt.tpe.toString != "Unit" =>
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
