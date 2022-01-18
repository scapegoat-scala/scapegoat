package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Marconi Lanna
 */
@SuppressWarnings(Array("IncorrectlyNamedExceptions"))
class CatchException
    extends Inspection(
      text = "Catch exception",
      defaultLevel = Levels.Warning,
      description = "Checks for try blocks that catch exception.",
      explanation =
        "Did you intend to catch all exceptions? Consider catching a more specific exception class."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          def catchesException(cases: List[CaseDef]): Boolean = {
            cases.exists {
              // matches t : Exception
              case CaseDef(Bind(_, Typed(_, tpt)), _, _) if tpt.tpe =:= typeOf[Exception] => true
              // matches _ : Exception
              case CaseDef(Typed(_, tpt), _, _) if tpt.tpe =:= typeOf[Exception] => true
              case _                                                             => false
            }
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Try(_, cases, _) if catchesException(cases) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
