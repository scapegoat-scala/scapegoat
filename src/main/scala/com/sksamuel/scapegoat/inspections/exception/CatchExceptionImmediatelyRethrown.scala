package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat._

class CatchExceptionImmediatelyRethrown
    extends Inspection(
      text = "Caught Exception Immediately Rethrown",
      defaultLevel = Levels.Warning,
      description = "Checks for try-catch blocks that immediately rethrow caught exceptions.",
      explanation = "Immediately re-throwing a caught exception is equivalent to not catching it at all."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {

      override def postTyperTraverser: context.Traverser =
        new context.Traverser {
          import context.global._

          private def exceptionHandlers(cases: List[CaseDef]): List[(String, Tree)] = {
            cases.collect {
              // matches t : Exception
              case CaseDef(Bind(name, Typed(_, tpt)), _, body) if tpt.tpe =:= typeOf[Exception] =>
                (name.toString(), body)
              // matches t : Throwable
              case CaseDef(Bind(name, Typed(_, tpt)), _, body) if tpt.tpe =:= typeOf[Throwable] =>
                (name.toString(), body)
            }
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Try(_, catches, _) =>
                val exceptionBodies = exceptionHandlers(catches)
                exceptionBodies.collect {
                  case (exceptionName, Throw(Ident(name))) if name.toString() == exceptionName =>
                    context.warn(tree.pos, self)
                }
              case _ =>
                continue(tree)
            }
          }
        }
    }
}
