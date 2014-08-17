package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.{Levels, Inspection, InspectionContext, Inspector}

/** @author Stephen Samuel */
class SwallowedException extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def traverser = new context.Traverser {

      import context.global._

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(_, cases, _) =>
            cases collect {
              case cd@CaseDef(_, _, Literal(Constant(()))) => cd
            } foreach (casedef => {
              context.warn("Swallowed exception",
                tree.pos,
                Levels.Warning,
                "Exception is caught but not handled. Did you mean to swallow this exception?",
                SwallowedException.this)
            })
          case _ => continue(tree)
        }
      }
    }
  }
}