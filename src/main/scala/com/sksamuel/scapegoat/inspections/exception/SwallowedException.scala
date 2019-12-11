package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class SwallowedException extends Inspection("Empty catch block", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def warn(cd: CaseDef): Unit = {
        if (cd.body.toString == "()")
          context.warn(cd.pos, self, "Empty catch block " + cd.toString().take(100))
      }

      private def checkCatches(defs: List[CaseDef]) = defs.foreach {
        case CaseDef(Bind(TermName("ignored") | TermName("ignore"), _), _, _) =>
        case cdef @ CaseDef(_, _, Literal(Constant(()))) => warn(cdef)
        case _ =>
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(_, catches, _) => checkCatches(catches)
          case _                             => continue(tree)
        }
      }
    }
  }
}