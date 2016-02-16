package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.{ Inspection, InspectionContext, Inspector, Levels }

import scala.util.control.ControlThrowable

/** @author Marconi Lanna */
class CatchFatal extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      def isFatal(tpe: context.global.Type) = {
        tpe =:= typeOf[VirtualMachineError] ||
          tpe =:= typeOf[ThreadDeath] ||
          tpe =:= typeOf[InterruptedException] ||
          tpe =:= typeOf[LinkageError] ||
          tpe =:= typeOf[ControlThrowable]
      }

      def catchesFatal(cases: List[CaseDef]) = {
        cases.exists {
          // matches t : FatalException
          case CaseDef(Bind(_, Typed(_, tpt)), _, _) if isFatal(tpt.tpe) => true
          // matches _ : FatalException
          case CaseDef(Typed(_, tpt), _, _) if isFatal(tpt.tpe) => true
          case _ => false
        }
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(_, cases, _) if catchesFatal(cases) =>
            context.warn("Catch fatal exception",
              tree.pos,
              Levels.Warning,
              "Did you intend to catch a fatal exception, consider using scala.util.control.NonFatal: " +
                tree.toString().take(300), CatchFatal.this)
          case _ => continue(tree)
        }
      }
    }
  }
}
