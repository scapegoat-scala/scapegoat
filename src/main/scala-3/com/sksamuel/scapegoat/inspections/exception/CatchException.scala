package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.util.SourcePosition

@SuppressWarnings(Array("IncorrectlyNamedExceptions"))
class CatchException
    extends Inspection(
      text = "Catch exception",
      defaultLevel = Levels.Warning,
      description = "Checks for try blocks that catch exception.",
      explanation =
        "Did you intend to catch all exceptions? Consider catching a more specific exception class."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def catchesException(cases: List[CaseDef]): Boolean = {
        cases.exists {
          // matches t : Exception
          case CaseDef(Bind(_, Typed(_, tpt)), _, _)
              if tpt.tpe.typeSymbol.fullName.toString == "java.lang.Exception" =>
            true
          // matches _ : Exception
          case CaseDef(Typed(_, tpt), _, _)
              if tpt.tpe.typeSymbol.fullName.toString == "java.lang.Exception" =>
            true
          case _ => false
        }
      }

      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Try(_, cases, _) if catchesException(cases) =>
            feedback.warn(tree.sourcePos, self, tree.asSnippet.map(_.take(300)))
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
