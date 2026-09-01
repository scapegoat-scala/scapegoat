package com.sksamuel.scapegoat.inspections.exception

import scala.collection.mutable

import com.sksamuel.scapegoat.*
import dotty.tools.dotc.ast.Trees.*
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Types.Type
import dotty.tools.dotc.util.SourcePosition

class UnreachableCatch
    extends Inspection(
      text = "Unreachable catch",
      defaultLevel = Levels.Warning,
      description = "Checks for catch clauses that cannot be reached.",
      explanation = "One or more cases are unreachable."
    ) {

  import tpd.*

  def inspect(feedback: Feedback[SourcePosition], tree: tpd.Tree)(using ctx: Context): Unit = {
    val traverser = new InspectionTraverser {
      def isUnreachable(cases: List[CaseDef])(using Context): Boolean = {
        val types = mutable.HashSet[Type]()
        def check(tpe: Type, guard: Tree): Boolean = {
          if (types.exists(tpe <:< _))
            true
          else {
            if (guard == EmptyTree)
              types.add(tpe): Unit
            false
          }
        }
        cases.exists {
          // matches t : Throwable
          case CaseDef(Bind(_, Typed(_, tpt)), guard, _) => check(tpt.tpe, guard)
          case CaseDef(Typed(_, tpt), guard, _)
              if tpt.tpe.typeSymbol.fullName.toString == "java.lang.Throwable" =>
            check(tpt.tpe, guard)
          case _ => false
        }
      }

      def traverse(tree: Tree)(using Context): Unit = {
        tree match {
          case Try(_, cases, _) if isUnreachable(cases) =>
            feedback.warn(tree.sourcePos, self, tree.asSnippet.map(_.take(300)))
          case _ => traverseChildren(tree)
        }
      }
    }
    traverser.traverse(tree)
  }
}
