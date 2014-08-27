package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class SwallowedException extends Inspection {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def warn(cd: CaseDef): Unit = {
        if (cd.body.toString == "()")
          context.warn("Empty catch block", cd.pos, Levels.Warning,
            "Empty catch block " + cd.toString().take(100), SwallowedException.this)
      }

      private def checkCatches(defs: List[CaseDef]) = defs.foreach {
        case CaseDef(Bind(TermName("ignored") | TermName("ignore"), _), _, _) =>
        case cdef @ CaseDef(_, _, Literal(Constant(()))) => warn(cdef)
        case _ =>
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(body, catches, finalizer) => checkCatches(catches)
          case _                             => continue(tree)
        }
      }
    }
  }
}