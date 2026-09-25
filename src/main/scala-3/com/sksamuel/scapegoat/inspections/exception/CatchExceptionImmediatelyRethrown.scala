package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.util.SourcePosition

class CatchExceptionImmediatelyRethrown
    extends Inspection(
      text = "Caught Exception Immediately Rethrown",
      defaultLevel = Levels.Warning,
      description = "Checks for try-catch blocks that immediately rethrow caught exceptions.",
      explanation = "Immediately re-throwing a caught exception is equivalent to not catching it at all."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def exceptionHandlers(cases: List[CaseDef]): List[(String, Tree)] = {
        cases.collect {
          // matches t : Exception
          case CaseDef(Bind(name, Typed(_, tpt)), _, body)
              if tpt.tpe.typeSymbol.fullName.toString == "java.lang.Exception" =>
            (name.toString, body)
          // matches t : Throwable
          case CaseDef(Bind(name, Typed(_, tpt)), _, body)
              if tpt.tpe.typeSymbol.fullName.toString == "java.lang.Throwable" =>
            (name.toString, body)
        }
      }

      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Try(_, catches, _) =>
            val exceptionBodies = exceptionHandlers(catches)
            exceptionBodies.foreach {
              case (exceptionName, body) if body.symbol.name.toString == "throw" =>
                // Check if the thrown expression is just the caught exception
                val children = body match {
                  case Apply(_, args) => args
                  case _              => Nil
                }
                children match {
                  case List(Ident(name)) if name.toString == exceptionName =>
                    feedback.warn(tree.sourcePos, self, tree.asSnippet)
                  case _ =>
                }
              case _ =>
            }
            traverseChildren(tree)
          case _ =>
            traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
