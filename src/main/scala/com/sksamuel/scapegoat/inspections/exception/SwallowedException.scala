package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class SwallowedException extends Inspection("Empty catch block", Levels.Warning) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private def containsMaskingThrow(expectedCauseException: Name, trees: Seq[Tree]): Boolean = {
        trees.exists(tree => {
          val thisResult = tree match {
            case Throw(Apply(Select(New(_), _), args))
              if args.collect { case Ident(i: Name) if i == expectedCauseException => true }.isEmpty =>
              true
            case _ => false
          }
          thisResult || containsMaskingThrow(expectedCauseException, tree.children)
        })
      }

      private def checkCatches(defs: List[CaseDef]) = defs.foreach {
        case CaseDef(Bind(TermName("ignored") | TermName("ignore"), _), _, _) =>

        case cdef @ CaseDef(_, _, Literal(Constant(())))
          if cdef.body.toString == "()" =>
            context.warn(cdef.pos, self, "Empty catch block " + cdef.toString().take(100))

        case cdef @ CaseDef(Bind(caughtException, _), _, subtree)
          if containsMaskingThrow(caughtException, Seq(subtree)) =>
            context.warn(cdef.pos, self,
              "Masking exception by not passing the original exception as cause: " + cdef.toString().take(100))

        case _ =>
      }

      override def inspect(tree: Tree): Unit = {
        tree match {
          case Try(_, catches, _) => checkCatches(catches)
          case _                  => continue(tree)
        }
      }
    }
  }
}
