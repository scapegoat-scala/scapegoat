package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.{Levels, Inspection, Reporter}

import scala.reflect.runtime._

/** @author Stephen Samuel */
class ExpressionAsStatement extends Inspection {

  override def traverser(reporter: Reporter) = new universe.Traverser {

    import universe._

    private def checkStatements(statements: Seq[Tree]): Unit = {
      statements foreach {
        case Apply(Select(_, name), _) if name.toString == "<init>" =>
        case Assign(_, _) =>
        case stmt if stmt.isDef =>
        case stmt if stmt.tpe != null && stmt.tpe.toString != "Unit" =>
          reporter.warn("Expression as statement",
            stmt,
            Levels.Warning,
            "Expression as statement at " + stmt.toString().take(100))
        case _ =>
      }
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Block(statements, _) => checkStatements(statements)
        case ClassDef(_, _, _, Template((_, _, statements))) => checkStatements(statements)
        case ModuleDef(_, _, Template((_, _, statements))) => checkStatements(statements)
        case _ =>
      }
      super.traverse(tree)
    }
  }
}
