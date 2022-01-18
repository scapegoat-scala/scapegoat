package com.sksamuel.scapegoat.inspections.exception

import scala.collection.mutable

import com.sksamuel.scapegoat.{Inspection, InspectionContext, Inspector, Levels}

/**
 * @author
 *   Stephen Samuel
 */
class UnreachableCatch
    extends Inspection(
      text = "Unreachable catch",
      defaultLevel = Levels.Warning,
      description = "Checks for catch clauses that cannot be reached.",
      explanation = "One or more cases are unreachable."
    ) {

  def inspector(context: InspectionContext): Inspector =
    new Inspector(context) {
      override def postTyperTraverser: context.Traverser =
        new context.Traverser {

          import context.global._

          def isUnreachable(cases: List[CaseDef]): Boolean = {
            val types = mutable.HashSet[Type]()
            def check(tpe: Type, guard: Tree): Boolean = {
              if (types.exists(tpe <:< _))
                true
              else {
                if (guard == EmptyTree)
                  types.add(tpe)
                false
              }
            }
            cases.exists {
              // matches t : Throwable
              case CaseDef(Bind(_, Typed(_, tpt)), guard, _)                         => check(tpt.tpe, guard)
              case CaseDef(Typed(_, tpt), guard, _) if tpt.tpe =:= typeOf[Throwable] => check(tpt.tpe, guard)
              case _                                                                 => false
            }
          }

          override def inspect(tree: Tree): Unit = {
            tree match {
              case Try(_, cases, _) if isUnreachable(cases) =>
                context.warn(tree.pos, self, tree.toString.take(300))
              case _ => continue(tree)
            }
          }
        }
    }
}
