package com.sksamuel.scapegoat.inspections.exception

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.util.SourcePosition

class CatchNpe
    extends Inspection(
      text = "Catching NPE",
      defaultLevel = Levels.Error,
      description = "Checks for try blocks that catch null pointer exceptions.",
      explanation =
        "Avoid using null at all cost and you shouldn't need to catch NullPointerExceptions. Prefer Option to indicate potentially missing values and use Try to materialize exceptions thrown by any external libraries."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def catchesNpe(cases: List[CaseDef]): Boolean =
        cases.exists(c => c.pat.tpe.typeSymbol.fullName.toString == "java.lang.NullPointerException")

      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Try(_, catches, _) if catchesNpe(catches) =>
            feedback.warn(tree.sourcePos, self, tree.asSnippet)
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
