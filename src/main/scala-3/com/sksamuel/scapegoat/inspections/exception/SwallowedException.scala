package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Names.Name
import dotty.tools.dotc.util.SourcePosition

@SuppressWarnings(Array("IncorrectlyNamedExceptions"))
class SwallowedException
    extends Inspection(
      text = "Empty catch block",
      defaultLevel = Levels.Warning,
      description = "Finds catch blocks that don't handle caught exceptions.",
      explanation =
        "If you use a try/catch block to deal with an exception, you should handle all of the caught exceptions and if for some reason you're throwing another exception in the result, you should include the original exception as the cause."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def getTreeChildren(tree: Tree): Seq[Tree] = tree match {
        case Apply(fun, args)               => fun +: args
        case TypeApply(fun, args)           => fun +: args
        case Select(qualifier, _)           => Seq(qualifier)
        case Typed(expr, tpt)               => Seq(expr, tpt)
        case Block(stats, expr)             => stats :+ expr
        case If(cond, thenp, elsep)         => Seq(cond, thenp, elsep)
        case Assign(lhs, rhs)               => Seq(lhs, rhs)
        case CaseDef(pat, guard, body)      => Seq(pat, guard, body)
        case Try(block, catches, finalizer) => block +: (catches :+ finalizer)
        case _                              => Nil
      }

      def containsMaskingThrow(expectedCauseException: Name, trees: Seq[Tree])(using Context): Boolean = {
        trees.exists { tree =>
          val thisResult = tree match {
            // Check if this is a throw statement that creates a new exception without including the caught exception
            case t if t.symbol.name.toString == "throw" =>
              t match {
                case Apply(_, List(Apply(Select(New(_), _), args))) =>
                  // Check if args contains the expected exception
                  !args.exists {
                    case Ident(i: Name) if i == expectedCauseException => true
                    case _                                             => false
                  }
                case _ => false
              }
            case _ => false
          }
          thisResult || containsMaskingThrow(expectedCauseException, getTreeChildren(tree))
        }
      }

      def checkCatches(defs: List[CaseDef])(using Context): Unit =
        defs.foreach {
          case CaseDef(Bind(name, _), _, _) if name.toString == "ignored" || name.toString == "ignore" =>
          case cdef @ CaseDef(_, _, Literal(c)) if c.value == () && cdef.body.toString == "()"         =>
            feedback.warn(cdef.sourcePos, self, cdef.asSnippet.map(_.take(100)))
          case cdef @ CaseDef(Bind(caughtException, _), _, subtree)
              if containsMaskingThrow(caughtException, Seq(subtree)) =>
            feedback.warn(cdef.sourcePos, self, cdef.asSnippet.map(_.take(100)))
          case _ =>
        }

      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Try(_, catches, _) =>
            checkCatches(catches)
            traverseChildren(tree)
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
