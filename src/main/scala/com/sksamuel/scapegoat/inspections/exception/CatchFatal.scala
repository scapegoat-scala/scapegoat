package com.sksamuel.scapegoat.inspections.exception

import scala.util.control.ControlThrowable

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Marconi Lanna
 */
class CatchFatal
    extends Inspection(
      text = "Catch fatal exception",
      defaultLevel = Levels.Warning,
      description =
        "Checks for try blocks that catch fatal exceptions: VirtualMachineError, ThreadDeath, InterruptedException, LinkageError, ControlThrowable.",
      explanation =
        "Did you intend to catch a fatal exception? Consider using scala.util.control.NonFatal instead."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          def isFatal(tpe: context.global.Type): Boolean = {
            tpe =:= typeOf[VirtualMachineError] ||
            tpe =:= typeOf[ThreadDeath] ||
            tpe =:= typeOf[InterruptedException] ||
            tpe =:= typeOf[LinkageError] ||
            tpe =:= typeOf[ControlThrowable]
          }

          def catchesFatal(cases: List[CaseDef]): Boolean = {
            cases.exists {
              // matches t : FatalException
              case CaseDef(Bind(_, Typed(_, tpt)), _, _) if isFatal(tpt.tpe) => true
              // matches _ : FatalException
              case CaseDef(Typed(_, tpt), _, _) if isFatal(tpt.tpe) => true
              case _                                                => false
            }
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Try(_, cases, _) if catchesFatal(cases) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
