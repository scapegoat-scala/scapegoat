package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat._

/**
 * @author
 *   Stephen Samuel
 */
@SuppressWarnings(Array("IncorrectlyNamedExceptions"))
class SwallowedException
    extends Inspection(
      text = "Empty catch block",
      defaultLevel = Levels.Warning,
      description = "Finds catch blocks that don't handle caught exceptions.",
      explanation =
        "If you use a try/catch block to deal with an exception, you should handle all of the caught exceptions and if for some reason you're throwing another exception in the result, you should include the original exception as the cause."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          private def containsMaskingThrow(expectedCauseException: Name, trees: Seq[Tree]): Boolean = {
            trees.exists { tree =>
              val thisResult = tree match {
                case Throw(Apply(Select(New(_), _), args)) if args.collect {
                      case Ident(i: Name) if i == expectedCauseException => true
                    }.isEmpty =>
                  true
                case _ => false
              }
              thisResult || containsMaskingThrow(expectedCauseException, tree.children)
            }
          }

          private def checkCatches(defs: List[CaseDef]): Unit =
            defs.foreach {
              case CaseDef(Bind(TermName("ignored") | TermName("ignore"), _), _, _) =>
              case cdef @ CaseDef(_, _, Literal(Constant(()))) if cdef.body.toString == "()" =>
                context.warn(cdef.pos, self, cdef.toString.take(100))
              case cdef @ CaseDef(Bind(caughtException, _), _, subtree)
                  if containsMaskingThrow(caughtException, Seq(subtree)) =>
                context.warn(cdef.pos, self, cdef.toString.take(100))
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
