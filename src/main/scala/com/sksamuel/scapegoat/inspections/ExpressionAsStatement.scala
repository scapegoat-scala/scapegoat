package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Feedback, Inspection, Levels}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class ExpressionAsStatement extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    private def checkStatements(statements: Seq[Tree]): Unit = {
      statements foreach {
        case Apply(Select(_, name), _) if name.toString == "<init>" =>
        case Assign(_, _) =>
        case stmt if stmt.isDef =>
        case stmt if stmt.tpe != null && stmt.tpe.toString != "Unit" =>
          feedback.warn("Expression as statement",
            stmt.pos,
            Levels.Warning,
            "Expression as statement at " + stmt.toString().take(100))
        case _ =>
      }
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Block(statements, _) => checkStatements(statements)
        case ClassDef(_, _, _, Template(_, _, statements)) => checkStatements(statements)
        case ModuleDef(_, _, Template(_, _, statements)) => checkStatements(statements)
        case _ =>
      }
      super.traverse(tree)
    }
  }
}
