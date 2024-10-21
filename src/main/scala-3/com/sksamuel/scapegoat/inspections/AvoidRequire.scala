package com.sksamuel.scapegoat.inspections

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Symbols.Symbol
import dotty.tools.dotc.core.Types.TermRef
import dotty.tools.dotc.util.SourcePosition

class AvoidRequire
  extends Inspection(
    text = "Use of require",
    defaultLevel = Levels.Warning,
    description = "Use require in code.",
    explanation = "Using require throws an untyped Exception."
  ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Apply(ident: Ident, _) if ident.name.toString == "require" =>
            ident.tpe.normalizedPrefix match {
              case TermRef(tx, nm: Symbol)
                if nm.toString == "object Predef" &&
                  tx.normalizedPrefix.typeSymbol.name.toString == "<root>" =>
                feedback.warn(tree.sourcePos, self, tree.asSnippet)
              case x =>
            }
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}