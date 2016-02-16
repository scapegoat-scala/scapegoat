package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

/** @author Marconi Lanna */
class CatchException extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      def catchesException(cases: List[CaseDef]) = {
        cases.exists {
          // matches t : Exception
          case CaseDef(Bind(_, Typed(_, tpt)), _, _) if tpt.tpe =:= typeOf[Exception] => true
          // matches _ : Exception
          case CaseDef(Typed(_, tpt), _, _) if tpt.tpe =:= typeOf[Exception] => true
          case _ => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(_, cases, _) if catchesException(cases) =>
            context.warn("Catch exception",
              tree.pos,
              Levels.Warning,
              "Did you intend to catch all exceptions, consider catching a more specific exception class: " +
                tree.toString().take(300), CatchException.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
